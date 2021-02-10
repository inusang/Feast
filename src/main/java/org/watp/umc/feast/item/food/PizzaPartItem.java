package org.watp.umc.feast.item.food;

import org.watp.umc.feast.item.IProduceItem;
import org.watp.umc.feast.math.Constants;
import org.watp.umc.feast.tileentity.OvenTileEntity;

import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Rarity;
import net.minecraft.tileentity.TileEntity;

public class PizzaPartItem extends Item implements IProduceItem {
	public PizzaPartItem() {
		super(new Properties().group(ItemGroup.FOOD).rarity(Rarity.UNCOMMON).food(new Food.Builder().hunger(14).saturation(1.4f).build()));
	}
	
	@Override
	public Class<? extends TileEntity> produceIn() {
		return OvenTileEntity.class;
	}
	
	@Override
	public int progressCount() {
		return 20*Constants.TICK_EACH_SECOND;
	}
}
