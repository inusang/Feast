package org.watp.umc.feast.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import org.watp.umc.feast.tileentity.DairyMachineTileEntity;

import java.util.function.Supplier;

public class PacketDMProductionTargetC2S {
    private final BlockPos pos;
    private final String productionTargetName;

    public PacketDMProductionTargetC2S(PacketBuffer buffer) {
        this.pos=buffer.readBlockPos();
        this.productionTargetName=buffer.readString();
    }

    public PacketDMProductionTargetC2S(BlockPos pos, String productionTargetName) {
        this.pos=pos;
        this.productionTargetName=productionTargetName;
    }

    public void toBytes(PacketBuffer buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeString(productionTargetName);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DairyMachineTileEntity te=(DairyMachineTileEntity) ctx.get().getSender().world.getTileEntity(pos);
            if (te.isOperable()==true)
                te.setProductionTarget(productionTargetName);
        });
        ctx.get().setPacketHandled(true);
    }
}
