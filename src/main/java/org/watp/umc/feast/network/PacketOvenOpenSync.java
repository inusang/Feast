package org.watp.umc.feast.network;

import java.util.function.Supplier;

import org.watp.umc.feast.tileentity.OvenTileEntity;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketOvenOpenSync {
	private final BlockPos pos;
	private final int open;
	
	public PacketOvenOpenSync(PacketBuffer buffer) {
		this.pos=buffer.readBlockPos();
		this.open=buffer.readInt();
	}
	
	public PacketOvenOpenSync(BlockPos pos,int open) {
		this.pos=pos;
		this.open=open;
	}
	
	public void toBytes(PacketBuffer buffer) {
		buffer.writeBlockPos(pos);
		buffer.writeInt(open);
	}
	
	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(()-> {
			OvenTileEntity te=(OvenTileEntity) ctx.get().getSender().world.getTileEntity(pos);
			te.setOpen(open);
		});
	}
}
