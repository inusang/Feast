package org.watp.umc.feast.tileentity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.fml.network.PacketDistributor;
import org.lwjgl.system.CallbackI;
import org.watp.umc.feast.Feast;
import org.watp.umc.feast.block.DairyMachineBlock;
import org.watp.umc.feast.inventory.DairyMachineContainer;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
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
import org.watp.umc.feast.item.IProduceItem;
import org.watp.umc.feast.network.NetWorking;
import org.watp.umc.feast.network.PacketDMStatS2C;
import org.watp.umc.feast.registry.ItemRegistry;

import javax.annotation.Nonnull;

public class DairyMachineTileEntity extends TileEntity implements ICustomContainer,ITickableTileEntity {
	private ItemStackHandler dairyMachineMaterialSlot;
	private ItemStackHandler productionSlot;

	private LazyOptional<IItemHandler> dairyMachineMaterialSlotHolder=LazyOptional.of(()->dairyMachineMaterialSlot);
	private LazyOptional<IItemHandler> productionSlotHolder=LazyOptional.of(()->productionSlot);
	private LazyOptional<IItemHandler> allSlotHolder=LazyOptional.of(()->new CombinedInvWrapper(dairyMachineMaterialSlot,productionSlot));

	private boolean operable;
	private int progress;
	private int progressVisible;
	private Item productionTarget;

	public enum WorkMode implements IStringSerializable {
		CREAM(Feast.Items.CREAM), BUTTER(Feast.Items.BUTTER), CHEESE(Feast.Items.CHEESE), NONE(Items.AIR);

		private Item productionTarget;

		WorkMode(Item productionTarget) {
			this.productionTarget=productionTarget;
		}

		public Item getProductionTarget() {
			return this.productionTarget;
		}

		public static WorkMode getByItem(@Nonnull Item item) {
			if (item== Feast.Items.CREAM) return CREAM;
			else if (item==Feast.Items.BUTTER) return BUTTER;
			else if (item==Feast.Items.CHEESE) return CHEESE;
			else return NONE;
		}

		public static Item getProductionTargetByName(@Nonnull String name) {
			List<WorkMode> result=Arrays.asList(WorkMode.values()).stream().
					filter(workMode -> workMode.getProductionTarget().getRegistryName().toString().equals(name)).
					collect(Collectors.toList());
			return result.size()!=0 ? result.get(0).getProductionTarget() : Items.AIR;
		}

		@Override
		public String getString() {
			return this.productionTarget.getRegistryName().toString();
		}
	}

	public DairyMachineTileEntity() {
		super(Feast.TileEntities.DAIRY_MACHINE);
		dairyMachineMaterialSlot=new ItemStackHandler(1);
		productionSlot=new ItemStackHandler(1);
		operable=true;
		progress=0;
		progressVisible=0;
		productionTarget=WorkMode.NONE.getProductionTarget();
	}

	public Item getProductionTarget() {
		return productionTarget;
	}

	public void setProductionTarget(String itemName) {
		this.productionTarget=WorkMode.getProductionTargetByName(itemName);
	}

	public void setProductionTarget(Item item) {
		this.productionTarget=item;
	}

	public boolean isOperable() {
		return operable;
	}

	public void setOperable(boolean operable) {
		this.operable = operable;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
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
		if (productionTarget==WorkMode.NONE.getProductionTarget()) {
			return;
		}
		if (progress==0) {
			Item material=dairyMachineMaterialSlot.getStackInSlot(0).getItem();
			boolean sameProduction=productionTarget==productionSlot.getStackInSlot(0).getItem() || productionSlot.getStackInSlot(0).getItem()==Items.AIR;
			IProduceItem produceItem=(IProduceItem) productionTarget;
			boolean hasMax=produceItem.produceCount()+productionSlot.getStackInSlot(0).getCount()>productionSlot.getSlotLimit(0);
			if (material==Items.MILK_BUCKET && sameProduction && !hasMax) {
				dairyMachineMaterialSlot.extractItem(0, 1, false);
				dairyMachineMaterialSlot.insertItem(0, new ItemStack(Items.BUCKET), false);
				progressVisible=Math.round(++progress/(float)produceItem.progressCount()*44);
				this.world.setBlockState(this.pos, this.world.getBlockState(pos).with(DairyMachineBlock.WORKING, true));
				operable=false;
			}
			else {
				this.world.setBlockState(this.pos, this.world.getBlockState(pos).with(DairyMachineBlock.WORKING, false));
				operable=true;
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
				operable=true;
			}
		}
		this.markDirty();
		NetWorking.INSTANCE.send(PacketDistributor.ALL.noArg(),
				new PacketDMStatS2C(this.getPos(), operable, progress, progressVisible, productionTarget));
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		compound.put("feast:dm.material",dairyMachineMaterialSlot.serializeNBT());
		compound.put("feast:dm.production",productionSlot.serializeNBT());
		compound.putBoolean("feast:dm.operable",operable);
		compound.putInt("feast:dm.progress",progress);
		compound.putInt("feast:dm.productionTarget",Item.getIdFromItem(productionTarget));
		return compound;
	}
	
	@Override
	public void read(BlockState bs, CompoundNBT compound) {
		super.read(bs,compound);
		dairyMachineMaterialSlot.deserializeNBT(compound.getCompound("feast:dm.material"));
		productionSlot.deserializeNBT(compound.getCompound("feast:dm.production"));
		this.operable=compound.getBoolean("feast:dm.operable");
		this.progress=compound.getInt("feast:dm.progress");
		this.productionTarget=Item.getItemById(compound.getInt("feast:dm.productionTarget"));
	}
	
	@Override
	public Container createMenu(int windowId, PlayerInventory pi, PlayerEntity pe) {
		return new DairyMachineContainer(windowId, pi, this);
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
