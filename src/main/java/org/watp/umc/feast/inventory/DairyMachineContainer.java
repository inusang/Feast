package org.watp.umc.feast.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.watp.umc.feast.Feast;
import org.watp.umc.feast.inventory.slot.ICheckedSlot;
import org.watp.umc.feast.inventory.slot.ProductionSlot;
import org.watp.umc.feast.tileentity.DairyMachineTileEntity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class DairyMachineContainer extends CommonInteractContainer {
	private DairyMachineTileEntity te;
	
	@OnlyIn(Dist.CLIENT)
	public DairyMachineContainer(int windowId, PlayerInventory pi, PacketBuffer extraData) {
		this(windowId, pi , (DairyMachineTileEntity) Minecraft.getInstance().world.getTileEntity(extraData.readBlockPos()));
	}

	public DairyMachineContainer(int windowId, PlayerInventory pi, DairyMachineTileEntity te) {
		super(Feast.ContainerTypes.DAIRY_MACHINE, windowId);
		this.te=te;
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
	protected <T extends TileEntity> void bindOtherSlots(T te) {
		Capability<IItemHandler> itemHandlerCap=CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
		IItemHandler materialItemStack=te.getCapability(itemHandlerCap,Direction.UP).orElse(null);
		this.addSlot(new MRMMaterialSlot(materialItemStack,0,39,28));
		IItemHandler productionItemStack=te.getCapability(itemHandlerCap,Direction.DOWN).orElse(null);
		this.addSlot(new ProductionSlot(productionItemStack,0,121,28));
	}

	@Override
	protected void trackVars() {
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
			return item==Items.MILK_BUCKET;
		}
	}

}
