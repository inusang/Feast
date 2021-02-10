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
		INSTANCE=NetworkRegistry.newSimpleChannel(new ResourceLocation(Feast.MODID,"networking"),
				()->VERSION,version->version.equals(VERSION),version->version.equals(VERSION));
		INSTANCE.registerMessage(id++, PacketOvenOpenC2S.class, PacketOvenOpenC2S::toBytes, PacketOvenOpenC2S::new, PacketOvenOpenC2S::handle);
		INSTANCE.registerMessage(id++, PacketOvenStatS2C.class, PacketOvenStatS2C::toBytes, PacketOvenStatS2C::new, PacketOvenStatS2C::handle);
		INSTANCE.registerMessage(id++, PacketDMStatS2C.class, PacketDMStatS2C::toBytes, PacketDMStatS2C::new, PacketDMStatS2C::handle);
		INSTANCE.registerMessage(id++, PacketDMProductionTargetC2S.class, PacketDMProductionTargetC2S::toBytes, PacketDMProductionTargetC2S::new, PacketDMProductionTargetC2S::handle);
	}
}
