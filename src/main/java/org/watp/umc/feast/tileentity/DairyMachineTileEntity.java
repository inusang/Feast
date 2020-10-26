package org.watp.umc.feast.tileentity;

import java.util.Map;

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

public class DairyMachineTileEntity extends TileEntity implements ICustomContainer,ITickableTileEntity {
	private ItemStackHandler DairyMachineMaterialSlot;
	private ItemStackHandler productionSlot;
	
	private LazyOptional<IItemHandler> dairyMachineMaterialSlotHolder=LazyOptional.of(()->DairyMachineMaterialSlot);
	private LazyOptional<IItemHandler> productionSlotHolder=LazyOptional.of(()->productionSlot);
	private LazyOptional<IItemHandler> allSlotHolder=LazyOptional.of(()->new CombinedInvWrapper(DairyMachineMaterialSlot,productionSlot));
	
	private int progress;
	private int progressVisible;
	private Item productionTarget;
	
	public DairyMachineTileEntity() {
		super(TileEntityRegistry.tileEntityDairyMachine.get());
		DairyMachineMaterialSlot=new ItemStackHandler(1);
		productionSlot=new ItemStackHandler(1);
		progress=0;
		progressVisible=0;
	}
	
	public int getProgressVisible() {
		return this.progressVisible;
	}
	
	public void setProgressVisible(int progressVisible) {
		this.progressVisible=progressVisible;
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
		else {
			if (progress==0 && productionTarget==null) {
				Item material=DairyMachineMaterialSlot.getStackInSlot(0).getItem();
				Item production= DairyMachineRecipe.match(material);
				if (production!=null) {
					IProduceItem produce=(IProduceItem) production;
					int consumeCount=0;
					for (Map.Entry<Item,Integer> entry : produce.consume().entrySet()) {
						consumeCount=entry.getValue();
					}
					boolean hasMax=produce.produceCount()+productionSlot.getStackInSlot(0).getCount()>productionSlot.getSlotLimit(0);
					boolean sameProduction=production==productionSlot.getStackInSlot(0).getItem() || productionSlot.getStackInSlot(0).getItem()==Items.AIR;
					if (DairyMachineMaterialSlot.getStackInSlot(0).getCount()>=consumeCount && consumeCount!=0 && !hasMax && sameProduction) {
						DairyMachineMaterialSlot.extractItem(0,consumeCount,false);
						if (material==Items.MILK_BUCKET) {
							ItemStack bucket=new ItemStack(Items.BUCKET,1);
							DairyMachineMaterialSlot.insertItem(0,bucket,false);
						}
						this.productionTarget=production;
						progressVisible=Math.round(++progress/(float)produce.progressCount()*44);
						this.markDirty();
					}
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
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		compound.put("feast:mrm.material",DairyMachineMaterialSlot.serializeNBT());
		compound.put("feast:mrm.production",productionSlot.serializeNBT());
		compound.putInt("feast:mrm.progress",progress);
		compound.putInt("feast:mrm.productionTarget",Item.getIdFromItem(productionTarget));
		return compound;
	}
	
	/**
	 * read(BlockState,CompoundNBT)</br>
	 */
	@Override
	public void read(BlockState bs, CompoundNBT compound) {
		super.read(bs,compound);
		DairyMachineMaterialSlot.deserializeNBT(compound.getCompound("feast:mrm.material"));
		productionSlot.deserializeNBT(compound.getCompound("feast:mrm.production"));
		this.progress=compound.getInt("feast:mrm.progress");
		Item item=Item.getItemById(compound.getInt("feast:mrm.productionTarget"));
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
		allSlotHolder.invalidate();
	}
}
