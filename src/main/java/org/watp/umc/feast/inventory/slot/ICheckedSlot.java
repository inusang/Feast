package org.watp.umc.feast.inventory.slot;

import net.minecraft.item.Item;

public interface ICheckedSlot {
	boolean check(Item item);
}
