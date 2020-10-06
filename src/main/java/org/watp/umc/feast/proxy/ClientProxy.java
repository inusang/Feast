package org.watp.umc.feast.proxy;

import org.watp.umc.feast.client.gui.OvenGuiContainer;
import org.watp.umc.feast.registry.ContainerTypeRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class ClientProxy implements IProxy {
	@Override
	public void init() {
		ScreenManager.registerFactory(ContainerTypeRegistry.containerOven.get(),OvenGuiContainer::new);
	}
	
	@Override
	public World getClientWorld() {
		return Minecraft.getInstance().world;
	}
	
	@Override
	public PlayerEntity getClientPlayer() {
		return Minecraft.getInstance().player;
	}
}
