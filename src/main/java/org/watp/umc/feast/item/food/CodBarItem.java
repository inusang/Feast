package org.watp.umc.feast.item.food;

import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import org.watp.umc.feast.item.IProduceItem;
import org.watp.umc.feast.math.Constants;
import org.watp.umc.feast.tileentity.OvenTileEntity;

import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Rarity;
import net.minecraft.tileentity.TileEntity;

public class CodBarItem extends Item implements IProduceItem {
	public CodBarItem() {
		super(new Properties().group(ItemGroup.FOOD).rarity(Rarity.UNCOMMON).food(new Food.Builder().hunger(8).saturation(0.7f).effect(()->new EffectInstance(Effects.WATER_BREATHING,45*Constants.TICK_EACH_SECOND),1).build()));
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
