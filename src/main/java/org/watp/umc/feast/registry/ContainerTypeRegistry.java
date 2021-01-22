package org.watp.umc.feast.registry;

import org.watp.umc.feast.Feast;
import org.watp.umc.feast.inventory.DairyMachineContainer;
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
			()-> IForgeContainerType.create(
			(int windowId, PlayerInventory pi, PacketBuffer extraData)-> new OvenContainer(windowId, pi, extraData)));
	
	public static RegistryObject<ContainerType<DairyMachineContainer>> containerDairyMachine=containers.register("dairy_machine",
			()-> IForgeContainerType.create(
			(int windowId, PlayerInventory pi, PacketBuffer extraData)-> new DairyMachineContainer(windowId, pi, extraData)));
}
