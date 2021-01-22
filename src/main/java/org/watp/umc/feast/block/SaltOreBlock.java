package org.watp.umc.feast.block;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

import java.util.Set;

public class SaltOreBlock extends Block implements IGenBlock {
	public SaltOreBlock() {
		super(Properties.create(Material.ICE).hardnessAndResistance(1f).harvestTool(ToolType.PICKAXE).harvestLevel(1));
	}

	@Override
	public Set<String> genInBiome() {
		return Sets.newHashSet();
	}
}
