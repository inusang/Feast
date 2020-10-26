package org.watp.umc.feast.tileentity;

import org.watp.umc.feast.block.OvenBlock;
import org.watp.umc.feast.inventory.OvenContainer;
import org.watp.umc.feast.item.IProduceItem;
import org.watp.umc.feast.recipe.OvenRecipe;
import org.watp.umc.feast.registry.TileEntityRegistry;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public class OvenTileEntity extends TileEntity implements ICustomContainer,ITickableTileEntity {
	private ItemStackHandler materialSlot;
	private ItemStackHandler fuelSlot;
	private ItemStackHandler freezeSlot;
	private ItemStackHandler productionSlot;
	private int open;
	private int remainingEnergy;
	private int remainingCooling;
	private int progress;
	private int temperature;	//>=6400 boom!
	
	public static enum VisibleIntValueType {
		REMAINING_ENERGY,
		REMAINING_COOLING,
		PROGRESS,
		TEMPERATURE
	}
	
	private LazyOptional<IItemHandler> materialSlotHolder=LazyOptional.of(()->materialSlot);
	private LazyOptional<IItemHandler> fuelSlotHolder=LazyOptional.of(()->fuelSlot);
	private LazyOptional<IItemHandler> freezeSlotHolder=LazyOptional.of(()->freezeSlot);
	private LazyOptional<IItemHandler> productionSlotHolder=LazyOptional.of(()->productionSlot);
	private LazyOptional<IItemHandler> allSlotHolder=LazyOptional.of(()->new CombinedInvWrapper(materialSlot,fuelSlot,freezeSlot,productionSlot));
	
	public OvenTileEntity() {
		super(TileEntityRegistry.tileEntityOven.get());
		materialSlot=new ItemStackHandler(9);
		fuelSlot=new ItemStackHandler(1);
		freezeSlot=new ItemStackHandler(1);
		productionSlot=new ItemStackHandler(1);
		this.open=0;
		this.remainingEnergy=0;
		this.remainingCooling=0;
		this.progress=0;
		this.temperature=0;
	}
	
	public Integer getIntVisibleValue(VisibleIntValueType vt) {
		if (vt==VisibleIntValueType.REMAINING_ENERGY) return this.remainingEnergy;
		else if (vt==VisibleIntValueType.REMAINING_COOLING) return this.remainingCooling;
		else if (vt==VisibleIntValueType.PROGRESS) return this.progress;
		else if (vt==VisibleIntValueType.TEMPERATURE) return this.temperature;
		else throw new IllegalArgumentException("Illegal value type");
	}
	
	public int getOpen() {
		return this.open;
	}
	
	public void setProgress(int progress) {
		this.progress=progress;
	}
	
	public void setRemainingEnergy(int remainingEnergy) {
		this.remainingEnergy = remainingEnergy;
	}
	
	public void setRemainingCooling(int remainingCooling) {
		this.remainingCooling = remainingCooling;
	}
	
	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}
	
	public void setOpen(int open) {
		this.open=open;
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		Capability<IItemHandler> itemHandlerCap=CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
		if (cap==itemHandlerCap) {
			this.markDirty();
			if (side==Direction.UP) {
				return materialSlotHolder.cast();
			}
			else if (side==Direction.DOWN) {
				return productionSlotHolder.cast();
			}
			else if (side==Direction.NORTH) {
				return fuelSlotHolder.cast();
			}
			else if (side==Direction.SOUTH) {
				return freezeSlotHolder.cast();
			}
			else {
				return allSlotHolder.cast();
			}
		}
		return super.getCapability(cap, side);
	}
	
	@Override
	public void tick() {
		if (world.isRemote) {
			return;
		}
		tryMakeCool();
		if (open==0) {
			this.world.setBlockState(this.pos,this.world.getBlockState(pos).with(OvenBlock.OVEN_STAT,0));
		}
		else {
			//check recipe
			Item[] inputRecipe=new Item[9];
			for (int i=0;i<9;i++) {
				inputRecipe[i]=materialSlot.getStackInSlot(i).getItem();
			}
			Item production=OvenRecipe.match(inputRecipe);
			if (production==null) {
				progress=0;
			}
			
			//apply fuel and cooling
			ItemStack simuItem=fuelSlot.extractItem(0,1,true);
			boolean hasMax=productionSlot.getStackInSlot(0).getCount()==productionSlot.getSlotLimit(0);
			if (simuItem!=null && remainingEnergy==0 && production!=null && !hasMax) {
				fuelSlot.extractItem(0,1,false);
				addRemainingValue(RemainingValueType.ENERGY,simuItem);
			}
			simuItem=freezeSlot.extractItem(0,1,true);
			if (simuItem!=null && temperature>1600 && remainingCooling==0) {
				freezeSlot.extractItem(0,1,false);
				addRemainingValue(RemainingValueType.COOLING,simuItem);
			}
			
			//produce
			boolean sameProduction=production==productionSlot.getStackInSlot(0).getItem() || productionSlot.getStackInSlot(0).getItem()==Items.AIR;
			if (production!=null && !hasMax && remainingEnergy>0 && sameProduction) {
				if (progress<400) {
					progress++;
				}
				else if (progress==400) {
					IProduceItem productionItem=(IProduceItem) production;
					for (int i=0;i<9;i++) {
						materialSlot.extractItem(i,1,false);
					}
					ItemStack productionStack=new ItemStack(production,productionItem.produceCount());
					productionSlot.insertItem(0,productionStack,false);
					progress=0;
				}
				temperature=temperature<6400?temperature+3:temperature;
				if (temperature>=6400) {
					for (int i=0;i<9;i++) {
						materialSlot.extractItem(i,materialSlot.getStackInSlot(i).getCount(),false);
					}
					productionSlot.extractItem(0,productionSlot.getStackInSlot(0).getCount(),false);
					this.world.createExplosion(null,this.getPos().getX(),this.getPos().getY(),this.getPos().getZ(),2,Explosion.Mode.DESTROY);
					return;
				}
				remainingEnergy=remainingEnergy<2?0:remainingEnergy-2;
				if (temperature>4072 && remainingCooling==0) {
					this.world.setBlockState(this.pos,this.world.getBlockState(pos).with(OvenBlock.OVEN_STAT,3));
				}
				else {
					this.world.setBlockState(this.pos,this.world.getBlockState(pos).with(OvenBlock.OVEN_STAT,2));
				}
			}
			else {
				this.world.setBlockState(this.pos,this.world.getBlockState(pos).with(OvenBlock.OVEN_STAT,1));
			}
		}
		temperature=temperature>0?--temperature:0;
		remainingEnergy=remainingEnergy>0?--remainingEnergy:0;
		this.markDirty();
	}
	
	private void tryMakeCool() {
		if (remainingCooling>0) {
			if (temperature>0) {
				temperature=temperature<2?0:temperature-2;
				remainingCooling=remainingCooling<2?0:remainingCooling-2;
			}
			else {
				--remainingCooling;
			}
		}
	}
	
	private void addRemainingValue(RemainingValueType valueTp,ItemStack itemStack) {
		Item item=itemStack.getItem();
		if (valueTp==RemainingValueType.ENERGY) {
			if (item==Items.COAL) {
				remainingEnergy+=1600;
			}
			else if (item==Items.CHARCOAL) {
				remainingEnergy+=1600;
			}
			else if (item==Items.COAL_BLOCK) {
				remainingEnergy+=16000;
			}
		}
		else if (valueTp==RemainingValueType.COOLING) {
			if (item==Items.ICE) {
				remainingCooling+=3200;
			}
			else if (item==Items.PACKED_ICE) {
				remainingCooling+=6400;
			}
			else if (item==Items.BLUE_ICE) {
				remainingCooling+=16000;
			}
		}
	}
	
	private enum RemainingValueType {
		ENERGY,
		COOLING
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		compound.put("feast:oven.material",this.materialSlot.serializeNBT());
		compound.put("feast:oven.fuel",this.fuelSlot.serializeNBT());
		compound.put("feast:oven.freeze",this.freezeSlot.serializeNBT());
		compound.put("feast:oven.production",this.productionSlot.serializeNBT());
		compound.putInt("feast:oven.open",this.open);
		compound.putInt("feast:oven.remainingEnergy",this.remainingEnergy);
		compound.putInt("feast:oven.remainingCooling",this.remainingCooling);
		compound.putInt("feast:oven.progress",this.progress);
		compound.putInt("feast:oven.temperature",this.temperature);
		return compound;
	}
	
	/**
	 * func_name: read(BlockState,CompoundNBT)</br>
	 */
	@Override
	public void read(BlockState bs, CompoundNBT compound) {
		super.read(bs,compound);
		this.materialSlot.deserializeNBT(compound.getCompound("feast:oven.material"));
		this.fuelSlot.deserializeNBT(compound.getCompound("feast:oven.fuel"));
		this.freezeSlot.deserializeNBT(compound.getCompound("feast:oven.freeze"));
		this.productionSlot.deserializeNBT(compound.getCompound("feast:oven.production"));
		this.open=compound.getInt("feast:oven.open");
		this.remainingEnergy=compound.getInt("feast:oven.remainingEnergy");
		this.remainingCooling=compound.getInt("feast:oven.remainingCooling");
		this.progress=compound.getInt("feast:oven.progress");
		this.temperature=compound.getInt("feast:oven.temperature");
	}
	
	@Override
	public void openGUI(ServerPlayerEntity player) {
		if (!player.world.isRemote) {
			NetworkHooks.openGui(player, this, getPos());
		}
	}
	
	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("block.feast.oven");
	}
	
	@Override
	public Container createMenu(int windowId, PlayerInventory pi, PlayerEntity player) {
		return new OvenContainer(windowId,pi,this.world,this.pos);
	}
	
	@Override
	public void remove() {
		super.remove();
		materialSlotHolder.invalidate();
		fuelSlotHolder.invalidate();
		freezeSlotHolder.invalidate();
		productionSlotHolder.invalidate();
		allSlotHolder.invalidate();
	}
	
}
