package org.watp.umc.feast.inventory.slot;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ProductionSlot extends SlotItemHandler {
	public ProductionSlot(IItemHandler itemHandler,int index,int xPos,int yPos) {
		super(itemHandler,index,xPos,yPos);
	}
	
	@Override
	public boolean isItemValid(ItemStack stack) {
		return false;
	}
}
