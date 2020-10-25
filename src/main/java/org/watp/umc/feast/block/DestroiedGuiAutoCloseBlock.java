package org.watp.umc.feast.block;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public abstract class DestroiedGuiAutoCloseBlock extends Block {
    public DestroiedGuiAutoCloseBlock(Properties properties) {
        super(properties);
    }

    public void onDestroy(World world, BlockPos pos) {
        TileEntity te=world.getTileEntity(pos);
        if (te!=null) {
            IItemHandler inven = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).orElse(null);
            for (int i = 0; i < inven.getSlots(); i++) {
                InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), inven.getStackInSlot(i));
            }
            world.removeTileEntity(pos);
        }
        if (world.isRemote()) {
            //Minecraft.getInstance().currentScreen=null;
            Minecraft.getInstance().displayGuiScreen(null);
        }
    }
}
