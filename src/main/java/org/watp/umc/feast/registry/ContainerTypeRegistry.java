package org.watp.umc.feast.registry;

import org.watp.umc.feast.Feast;
import org.watp.umc.feast.inventory.MilkRefineMachineContainer;
import org.watp.umc.feast.inventory.OvenContainer;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class ContainerTypeRegistry {
	
	public static final DeferredRegister<ContainerType<?>> containers=DeferredRegister.create(ForgeRegistries.CONTAINERS,Feast.MODID);
	
	public static RegistryObject<ContainerType<OvenContainer>> containerOven=containers.register("oven",
			()->{ return IForgeContainerType.create(
			(int windowId,PlayerInventory pi,PacketBuffer extraData)->{ return new OvenContainer(windowId,pi,Minecraft.getInstance().world,extraData.readBlockPos()); }); });
	
	public static RegistryObject<ContainerType<MilkRefineMachineContainer>> containerMilkRefineMachine=containers.register("milk_refine_machine",
			()->{ return IForgeContainerType.create(
			(int windowId,PlayerInventory pi,PacketBuffer extraData)->{ return new MilkRefineMachineContainer(windowId,pi,Minecraft.getInstance().world,extraData.readBlockPos()); }); });
}
