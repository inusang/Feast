package org.watp.umc.feast.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class SaltOreBlock extends Block {
	public SaltOreBlock() {
		super(Properties.create(Material.ICE).hardnessAndResistance(1f).harvestTool(ToolType.PICKAXE).harvestLevel(1));
	}
}
