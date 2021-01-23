package org.watp.umc.feast.block;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import java.util.Set;

public class CabbageBlock extends Block implements IGenBlock {
	public CabbageBlock() {
		super(Properties.create(Material.GOURD));
	}

	@Override
	public Set<String> genInBiome() {
		return Sets.newHashSet("minecraft:plains",
				"minecraft:sunflower_plains");
	}
}
