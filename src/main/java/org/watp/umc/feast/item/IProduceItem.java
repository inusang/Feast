package org.watp.umc.feast.item;

import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;

/**
 * use when produced by feast mod machine
 * 
 */
public interface IProduceItem {
	Class<? extends TileEntity> produceIn();
	
	default Map<Item,Integer> consume() {
		return null;
	}
	
	default int produceCount() {
		return 1;
	}
	
	int progressCount();
}
