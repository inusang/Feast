package org.watp.umc.feast.inventory;

import org.watp.umc.feast.Feast;
import org.watp.umc.feast.inventory.slot.ICheckedSlot;
import org.watp.umc.feast.inventory.slot.ProductionSlot;
import org.watp.umc.feast.registry.ContainerTypeRegistry;
import org.watp.umc.feast.tileentity.DairyMachineTileEntity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class DairyMachineContainer extends CommonInteractContainer {
	private DairyMachineTileEntity te;
	
	public DairyMachineContainer(int windowId, PlayerInventory pi, World world, BlockPos pos) {
		super(ContainerTypeRegistry.containerDairyMachine.get(),windowId);
		this.te=(DairyMachineTileEntity) world.getTileEntity(pos);
		this.bindPlayerInventory(pi);
		this.bindOtherSlots(te);
		this.trackVars();
	}
	
	public DairyMachineTileEntity getTileEntity() {
		return this.te;
	}
	
	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return true;
	}
	
	@Override
	protected <T extends TileEntity> void bindOtherSlots(final T te) {
		Capability<IItemHandler> itemHandlerCap=CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
		IItemHandler materialItemStack=te.getCapability(itemHandlerCap,Direction.UP).orElse(null);
		this.addSlot(new MRMMaterialSlot(materialItemStack,0,39,33));
		IItemHandler productionItemStack=te.getCapability(itemHandlerCap,Direction.DOWN).orElse(null);
		this.addSlot(new ProductionSlot(productionItemStack,0,121,33));
	}
	
	@Override
	protected void trackVars() {
		this.trackInt(new IntReferenceHolder() {
			@Override
			public void set(int value) {
				te.setProgressVisible(value);
			}
			
			@Override
			public int get() {
				return te.getProgressVisible()  & 0xffff;
			}
		});
	}
	
	private class MRMMaterialSlot extends SlotItemHandler implements ICheckedSlot {
		public MRMMaterialSlot(IItemHandler itemHandler,int index,int xPos,int yPos) {
			super(itemHandler,index,xPos,yPos);
		}
		
		@Override
		public boolean isItemValid(ItemStack stack) {
			return super.isItemValid(stack) && check(stack.getItem());
		}
		
		@Override
		public boolean check(Item item) {
			return item==Items.MILK_BUCKET || item==Feast.Items.CREAM || item==Feast.Items.BUTTER;
		}
	}
}
