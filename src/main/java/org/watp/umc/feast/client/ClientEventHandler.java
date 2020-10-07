package org.watp.umc.feast.client;

import org.watp.umc.feast.Feast;
import org.watp.umc.feast.client.gui.DairyRefineMachineGuiContainer;
import org.watp.umc.feast.client.gui.OvenGuiContainer;
import org.watp.umc.feast.registry.ContainerTypeRegistry;

import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientEventHandler {
	
	@EventBusSubscriber(modid = Feast.MODID,bus = EventBusSubscriber.Bus.MOD , value = Dist.CLIENT)
	private static class EventHandlers {
		@SubscribeEvent
		public static void registerClientGuiFactories(final FMLClientSetupEvent event) {
			ScreenManager.registerFactory(ContainerTypeRegistry.containerOven.get(),OvenGuiContainer::new);
			ScreenManager.registerFactory(ContainerTypeRegistry.containerMilkRefineMachine.get(), DairyRefineMachineGuiContainer::new);
		}
	}
}
