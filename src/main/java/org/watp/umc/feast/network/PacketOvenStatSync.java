package org.watp.umc.feast.network;

import java.util.function.Supplier;

import org.watp.umc.feast.tileentity.OvenTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketOvenStatSync {
	private final BlockPos pos;
	private final int progress;
	private final int temperature;
	private final int remainingEnergy;
	private final int remainingCooling;
	private final int open;
	
	public PacketOvenStatSync(PacketBuffer buffer) {
		this.pos=buffer.readBlockPos();
		this.progress=buffer.readInt();
		this.temperature=buffer.readInt();
		this.remainingEnergy=buffer.readInt();
		this.remainingCooling=buffer.readInt();
		this.open=buffer.readInt();
	}
	
	public PacketOvenStatSync(BlockPos pos,
			int progress,int temperature,int remainingEnergy,int remainingCooling,int open) {
		this.pos=pos;
		this.progress=progress;
		this.temperature=temperature;
		this.remainingEnergy=remainingEnergy;
		this.remainingCooling=remainingCooling;
		this.open=open;
	}
	
	public void toBytes(PacketBuffer buffer) {
		buffer.writeBlockPos(pos);
		buffer.writeInt(progress);
		buffer.writeInt(temperature);
		buffer.writeInt(remainingEnergy);
		buffer.writeInt(remainingCooling);
		buffer.writeInt(open);
	}
	
	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(()-> {
			OvenTileEntity te=(OvenTileEntity) Minecraft.getInstance().world.getTileEntity(pos);
			te.setProgress(progress);
			te.setTemperature(temperature);
			te.setRemainingEnergy(remainingEnergy);
			te.setRemainingCooling(remainingCooling);
			te.setOpen(open);
		});
	}
}
