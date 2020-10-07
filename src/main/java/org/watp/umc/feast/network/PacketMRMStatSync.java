package org.watp.umc.feast.network;

import java.util.function.Supplier;

import org.watp.umc.feast.tileentity.DairyMachineTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketMRMStatSync {
	private final BlockPos pos;
	private final int progressVisible;
	
	public PacketMRMStatSync(PacketBuffer buf) {
		this.pos=buf.readBlockPos();
		this.progressVisible=buf.readInt();
	}
	
	public PacketMRMStatSync(BlockPos pos,int progressVisible) {
		this.pos=pos;
		this.progressVisible=progressVisible;
	}
	
	public void toBytes(PacketBuffer buf) {
		buf.writeBlockPos(pos);
		buf.writeInt(progressVisible);
	}
	
	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(()-> {
			DairyMachineTileEntity te=(DairyMachineTileEntity) Minecraft.getInstance().world.getTileEntity(pos);
			te.setProgressVisible(progressVisible);
		});
	}
}
