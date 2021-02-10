package org.watp.umc.feast.item;

import java.util.Map;

import net.minecraft.item.Items;
import org.watp.umc.feast.math.Constants;
import org.watp.umc.feast.tileentity.DairyMachineTileEntity;

import com.google.common.collect.Maps;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntity;

public class ButterItem extends Item implements IProduceItem {
	public ButterItem() {
		super(new Properties().group(ItemGroup.MISC));
	}
	
	@Override
	public Class<? extends TileEntity> produceIn() {
		return DairyMachineTileEntity.class;
	}
	
	@Override
	public Map<Item, Integer> consume() {
		Map<Item,Integer> map=Maps.newHashMap();
		map.put(Items.MILK_BUCKET,1);
		return map;
	}
	
	@Override
	public int produceCount() {
		return 2;
	}
	
	@Override
	public int progressCount() {
		return 150*Constants.TICK_EACH_SECOND;
	}
}
