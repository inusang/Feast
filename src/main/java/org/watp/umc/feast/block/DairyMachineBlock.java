package org.watp.umc.feast.block;

import org.watp.umc.feast.network.NetWorking;
import org.watp.umc.feast.network.PacketDMStatSync;
import org.watp.umc.feast.tileentity.DairyMachineTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class DairyMachineBlock extends Block {
	protected static final DirectionProperty FACING=HorizontalBlock.HORIZONTAL_FACING;
	
	public DairyMachineBlock() {
		super(Properties.create(Material.IRON).hardnessAndResistance(3f).harvestTool(ToolType.PICKAXE).harvestLevel(1));
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(FACING);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
		if (!world.isRemote()) {
			final DairyMachineTileEntity te=(DairyMachineTileEntity) world.getTileEntity(pos);
			if (te!=null) {
				NetWorking.INSTANCE.send(PacketDistributor.SERVER.noArg()
						,new PacketDMStatSync(pos,te.getProgressVisible()));
				te.openGUI((ServerPlayerEntity)player);
			}
			return ActionResultType.CONSUME;
		}
		else {
			return ActionResultType.SUCCESS;
		}
	}
	
	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock()!=newState.getBlock()) {
			TileEntity te=world.getTileEntity(pos);
			IItemHandler inven=te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,null).orElse(null);
			InventoryHelper.spawnItemStack(world,pos.getX(),pos.getY(),pos.getZ(),inven.getStackInSlot(0));
		}
		if (state.hasTileEntity() && (state.getBlock() != newState.getBlock() || !newState.hasTileEntity())) {
	         world.removeTileEntity(pos);
	    }
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new DairyMachineTileEntity();
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
}
