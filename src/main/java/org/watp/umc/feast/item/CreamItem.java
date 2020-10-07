package org.watp.umc.feast.item;

import java.util.Map;

import org.watp.umc.feast.math.Constants;
import org.watp.umc.feast.tileentity.DairyRefineMachineTileEntity;

import com.google.common.collect.Maps;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;

public class CreamItem extends Item implements IProduceItem {
	public CreamItem() {
		super(new Properties().group(ItemGroup.MISC));
	}
	
	@Override
	public Class<? extends TileEntity> produceIn() {
		return DairyRefineMachineTileEntity.class;
	}
	
	@Override
	public Map<Item, Integer> consume() {
		Map<Item,Integer> map=Maps.newHashMap();
		map.put(Items.MILK_BUCKET,1);
		return map;
	}
	
	@Override
	public int produceCount() {
		return 8;
	}
	
	@Override
	public int progressCount() {
		return 180*Constants.TICK_EACH_SECOND;
	}
}
