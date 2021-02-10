package org.watp.umc.feast.network;

import java.util.function.Supplier;

import net.minecraft.item.Item;
import org.watp.umc.feast.tileentity.DairyMachineTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketDMStatS2C {
	private final BlockPos pos;
	private final boolean operable;
	private final int progress;
	private final int progressVisible;
	private final String productionTargetName;
	
	public PacketDMStatS2C(PacketBuffer buf) {
		this.pos=buf.readBlockPos();
		this.operable=buf.readBoolean();
		this.progress=buf.readInt();
		this.progressVisible=buf.readInt();
		this.productionTargetName=buf.readString();
	}
	
	public PacketDMStatS2C(BlockPos pos, boolean operable, int progress, int progressVisible, Item productionTarget) {
		this.pos=pos;
		this.operable=operable;
		this.progress=progress;
		this.progressVisible=progressVisible;
		this.productionTargetName=productionTarget.getRegistryName().toString();
	}
	
	public void toBytes(PacketBuffer buf) {
		buf.writeBlockPos(pos);
		buf.writeBoolean(operable);
		buf.writeInt(progress);
		buf.writeInt(progressVisible);
		buf.writeString(productionTargetName);
	}
	
	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(()-> {
			DairyMachineTileEntity te=(DairyMachineTileEntity) Minecraft.getInstance().world.getTileEntity(pos);
			te.setOperable(operable);
			te.setProgress(progress);
			te.setProgressVisible(progressVisible);
			te.setProductionTarget(productionTargetName);
		});
		ctx.get().setPacketHandled(true);
	}
}
