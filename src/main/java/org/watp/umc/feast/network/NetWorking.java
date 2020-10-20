package org.watp.umc.feast.network;

import org.watp.umc.feast.Feast;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetWorking {
	public static SimpleChannel INSTANCE;
	
	private static final String VERSION="1.0";
	private static int id=0;
	
	public static void registerMessage() {
		INSTANCE=NetworkRegistry.newSimpleChannel(new ResourceLocation(Feast.MODID,"feastdatasync"),
				()->VERSION,version->version.equals(VERSION),version->version.equals(VERSION));
		INSTANCE.registerMessage(id++,PacketOvenOpenSync.class,PacketOvenOpenSync::toBytes,PacketOvenOpenSync::new,PacketOvenOpenSync::handle);
		INSTANCE.registerMessage(id++,PacketOvenStatSync.class,PacketOvenStatSync::toBytes,PacketOvenStatSync::new,PacketOvenStatSync::handle);
		INSTANCE.registerMessage(id++,PacketDMStatSync.class, PacketDMStatSync::toBytes, PacketDMStatSync::new, PacketDMStatSync::handle);
	}
}
