package org.watp.umc.feast.registry;

import org.watp.umc.feast.Feast;
import org.watp.umc.feast.tileentity.MilkRefineMachineTileEntity;
import org.watp.umc.feast.tileentity.OvenTileEntity;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class TileEntityRegistry {
	public static final DeferredRegister<TileEntityType<?>> TileEntities=DeferredRegister.create(ForgeRegistries.TILE_ENTITIES,Feast.MODID);
	
	public static RegistryObject<TileEntityType<OvenTileEntity>> tileEntityOven=TileEntities.register("oven",
			()->TileEntityType.Builder.create(OvenTileEntity::new, BlockRegistry.blockOven.get()).build(null));
	
	public static RegistryObject<TileEntityType<MilkRefineMachineTileEntity>> tileEntityMilkRefineMachine=TileEntities.register("milk_refine_machine",
			()->TileEntityType.Builder.create(MilkRefineMachineTileEntity::new, BlockRegistry.blockMilkRefineMachine.get()).build(null));
	
}
