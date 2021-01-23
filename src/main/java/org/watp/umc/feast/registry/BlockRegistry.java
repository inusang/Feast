package org.watp.umc.feast.registry;

import org.watp.umc.feast.Feast;
import org.watp.umc.feast.block.DairyMachineBlock;
import org.watp.umc.feast.block.OvenBlock;
import org.watp.umc.feast.block.SaltOreBlock;

import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class BlockRegistry {
	public static final DeferredRegister<Block> BLOCKS=DeferredRegister.create(ForgeRegistries.BLOCKS,Feast.MODID);

	public static RegistryObject<Block> blockOven=BLOCKS.register("oven",OvenBlock::new);
	public static RegistryObject<Block> blockDairyMachine=BLOCKS.register("dairy_machine", DairyMachineBlock::new);
	
	public static RegistryObject<Block> blockSaltOre=BLOCKS.register("salt_ore",SaltOreBlock::new);
	
}
