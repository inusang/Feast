package org.watp.umc.feast.item.food;

import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public class CookedEggItem extends Item {
    public CookedEggItem() {
        super(new Properties().group(ItemGroup.FOOD).food(new Food.Builder().hunger(1).saturation(0.2f).build()));
    }
}
