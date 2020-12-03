package org.watp.umc.feast.tileentity;

import java.util.Map;

import net.minecraft.util.IStringSerializable;
import org.watp.umc.feast.Feast;
import org.watp.umc.feast.block.DairyMachineBlock;
import org.watp.umc.feast.inventory.DairyMachineContainer;
import org.watp.umc.feast.item.IProduceItem;
import org.watp.umc.feast.recipe.DairyMachineRecipe;
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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;
import javax.swing.plaf.basic.BasicButtonUI;

public class DairyMachineTileEntity extends TileEntity implements ICustomContainer,ITickableTileEntity {
	private ItemStackHandler dairyMachineMaterialSlot;
	private ItemStackHandler productionSlot;
	private ItemStackHandler collectionSlot;
	
	private LazyOptional<IItemHandler> dairyMachineMaterialSlotHolder=LazyOptional.of(()->dairyMachineMaterialSlot);
	private LazyOptional<IItemHandler> productionSlotHolder=LazyOptional.of(()->productionSlot);
	private LazyOptional<IItemHandler> collectionSlotHolder=LazyOptional.of(()->collectionSlot);
	private LazyOptional<IItemHandler> allSlotHolder=LazyOptional.of(()->new CombinedInvWrapper(dairyMachineMaterialSlot,productionSlot,collectionSlot));

	private int progress;
	private int progressVisible;
	private Item productionTarget;

	public enum WorkMode implements IStringSerializable {
		CREAM("cream"), BUTTER("butter"), CHEESE("cheese"), NONE("none");

		private String name;

		WorkMode(String name) {
			this.name=name;
		}

		public static WorkMode get(@Nonnull Item item) {
			if (item== Feast.Items.CREAM) return CREAM;
			else if (item==Feast.Items.BUTTER) return BUTTER;
			else if (item==Feast.Items.CHEESE) return CHEESE;
			else return NONE;
		}

		@Override
		public String getString() {
			return this.name;
		}
	}

	public DairyMachineTileEntity() {
		super(TileEntityRegistry.tileEntityDairyMachine.get());
		dairyMachineMaterialSlot=new ItemStackHandler(1);
		productionSlot=new ItemStackHandler(1);
		collectionSlot=new ItemStackHandler(9);
		progress=0;
		progressVisible=0;
	}

	public WorkMode getWorkMode() {
		return WorkMode.get(productionTarget);
	}
	
	public int getProgressVisible() {
		return this.progressVisible;
	}
	
	public void setProgressVisible(int progressVisible) {
		this.progressVisible=progressVisible;
	}

	public <T> LazyOptional<T> getCollectionSlotHolder() {
		return this.collectionSlotHolder.cast();
	}
	
	@Override
		public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		Capability<IItemHandler> itemHandlerCap=CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
		if (cap==itemHandlerCap) {
			this.markDirty();
			if (side==Direction.UP) {
				return dairyMachineMaterialSlotHolder.cast();
			}
			else if (side==Direction.DOWN) {
				return productionSlotHolder.cast();
			}
			else {
				return allSlotHolder.cast();
			}
		}
		return super.getCapability(cap, side);
	}
	
	@Override
	public void tick() {
		if (world.isRemote()) {
			return;
		}
		if (progress==0 && productionTarget==null) {
			Item material=dairyMachineMaterialSlot.getStackInSlot(0).getItem();
			Item production= DairyMachineRecipe.match(material);
			if (production!=null) {
				IProduceItem produce=(IProduceItem) production;
				int consumeCount=0;
				for (Map.Entry<Item,Integer> entry : produce.consume().entrySet()) {
					consumeCount=entry.getValue();
				}
				boolean hasMax=produce.produceCount()+productionSlot.getStackInSlot(0).getCount()>productionSlot.getSlotLimit(0);
				boolean sameProduction=production==productionSlot.getStackInSlot(0).getItem() || productionSlot.getStackInSlot(0).getItem()==Items.AIR;
				if (dairyMachineMaterialSlot.getStackInSlot(0).getCount()>=consumeCount && consumeCount!=0 && !hasMax && sameProduction) {
					dairyMachineMaterialSlot.extractItem(0,consumeCount,false);
					if (material==Items.MILK_BUCKET) {
						ItemStack bucket=new ItemStack(Items.BUCKET,1);
						dairyMachineMaterialSlot.insertItem(0,bucket,false);
					}
					this.productionTarget=production;
					progressVisible=Math.round(++progress/(float)produce.progressCount()*44);
					this.world.setBlockState(this.pos, this.world.getBlockState(pos).with(DairyMachineBlock.WORKING, true));
					this.markDirty();
				}
				else {
					this.world.setBlockState(this.pos, this.world.getBlockState(pos).with(DairyMachineBlock.WORKING, false));
				}
			}
			else {
				this.world.setBlockState(this.pos, this.world.getBlockState(pos).with(DairyMachineBlock.WORKING, false));
			}
		}
		else {
			IProduceItem produce=(IProduceItem) this.productionTarget;
			if (progress<produce.progressCount()) {
				progressVisible=Math.round(++progress/(float)produce.progressCount()*44);
			}
			else {
				ItemStack production=new ItemStack(productionTarget,produce.produceCount());
				productionSlot.insertItem(0,production,false);
				progressVisible=progress=0;
				productionTarget=null;
			}
			this.markDirty();
		}
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		compound.put("feast:dm.material",dairyMachineMaterialSlot.serializeNBT());
		compound.put("feast:dm.production",productionSlot.serializeNBT());
		compound.put("feast:dm.collection",collectionSlot.serializeNBT());
		compound.putInt("feast:dm.progress",progress);
		compound.putInt("feast:dm.productionTarget",Item.getIdFromItem(productionTarget));
		return compound;
	}
	
	@Override
	public void read(BlockState bs, CompoundNBT compound) {
		super.read(bs,compound);
		dairyMachineMaterialSlot.deserializeNBT(compound.getCompound("feast:dm.material"));
		productionSlot.deserializeNBT(compound.getCompound("feast:dm.production"));
		collectionSlot.deserializeNBT(compound.getCompound("feast:dm.collection"));
		this.progress=compound.getInt("feast:dm.progress");
		Item item=Item.getItemById(compound.getInt("feast:dm.productionTarget"));
		this.productionTarget=item==Items.AIR?null:item;
	}
	
	@Override
	public Container createMenu(int windowId, PlayerInventory pi, PlayerEntity pe) {
		return new DairyMachineContainer(windowId,pi,this.world,this.pos);
	}
	
	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("block.feast.dairy_machine");
	}
	
	@Override
	public void openGUI(ServerPlayerEntity player) {
		if (!player.world.isRemote()) {
			NetworkHooks.openGui(player,this,this.getPos());
		}
	}
	
	@Override
	public void remove() {
		super.remove();
		dairyMachineMaterialSlotHolder.invalidate();
		productionSlotHolder.invalidate();
		collectionSlotHolder.invalidate();
		allSlotHolder.invalidate();
	}
}
