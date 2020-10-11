package org.watp.umc.feast.item.food;

import java.util.Map;

import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import org.watp.umc.feast.Feast;
import org.watp.umc.feast.item.IProduceItem;
import org.watp.umc.feast.math.Constants;
import org.watp.umc.feast.tileentity.DairyMachineTileEntity;

import com.google.common.collect.Maps;

import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Rarity;
import net.minecraft.tileentity.TileEntity;

public class CheeseItem extends Item implements IProduceItem {
	public CheeseItem() {
		super(new Properties().group(ItemGroup.FOOD).rarity(Rarity.UNCOMMON).food(new Food.Builder().hunger(7).saturation(1f).effect(()->new EffectInstance(Effects.SLOWNESS,20*Constants.TICK_EACH_SECOND),0.2f).build()));
	}
	
	@Override
	public Class<? extends TileEntity> produceIn() {
		return DairyMachineTileEntity.class;
	}
	
	@Override
	public Map<Item, Integer> consume() {
		Map<Item,Integer> map=Maps.newHashMap();
		map.put(Feast.Items.BUTTER,1);
		return map;
	}
	
	@Override
	public int produceCount() {
		return 2;
	}
	
	@Override
	public int progressCount() {
		return 180*Constants.TICK_EACH_SECOND;
	}
}
