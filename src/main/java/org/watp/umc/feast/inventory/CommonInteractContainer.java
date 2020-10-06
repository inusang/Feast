package org.watp.umc.feast.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public abstract class CommonInteractContainer extends Container {
	public CommonInteractContainer(ContainerType<?> container,int windowId) {
		super(container,windowId);
	}
	
	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return true;
	}
	
	protected void bindPlayerInventory(PlayerInventory pi) {
		for (int i=0;i<3;++i) {
			for (int j=0;j<9;++j) {
				this.addSlot(new Slot(pi,j+i*9+9,8+j*18,84+i*18));
			}
		}
		for (int k=0;k<9;++k) {
			this.addSlot(new Slot(pi,k,8+k*18,142));
		}
	}
	
	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
		return ItemStack.EMPTY;
	}
	
	abstract protected <T extends TileEntity> void bindOtherSlots(T te);
	
	abstract protected void trackVars();
}
