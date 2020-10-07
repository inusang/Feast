package org.watp.umc.feast.item;

import java.util.Map;

import org.watp.umc.feast.Feast;
import org.watp.umc.feast.math.Constants;
import org.watp.umc.feast.tileentity.DairyRefineMachineTileEntity;

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
		return DairyRefineMachineTileEntity.class;
	}
	
	@Override
	public Map<Item, Integer> consume() {
		Map<Item,Integer> map=Maps.newHashMap();
		map.put(Feast.Items.CREAM,4);
		return map;
	}
	
	@Override
	public int produceCount() {
		return 1;
	}
	
	@Override
	public int progressCount() {
		return 240*Constants.TICK_EACH_SECOND;
	}
}
