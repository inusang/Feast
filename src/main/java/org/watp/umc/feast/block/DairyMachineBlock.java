package org.watp.umc.feast.block;

import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.Explosion;
import net.minecraft.world.IWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.watp.umc.feast.network.NetWorking;
import org.watp.umc.feast.network.PacketDMStatSync;
import org.watp.umc.feast.tileentity.DairyMachineTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
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

public class DairyMachineBlock extends DestroyedGuiAutoCloseBlock {
	protected static final DirectionProperty FACING=HorizontalBlock.HORIZONTAL_FACING;

	public DairyMachineBlock() {
		super(Properties.create(Material.IRON).hardnessAndResistance(3f).harvestTool(ToolType.PICKAXE).harvestLevel(1).notSolid());
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

	public VoxelShape func_230322_a_(BlockState p_230322_1_, IBlockReader p_230322_2_, BlockPos p_230322_3_, ISelectionContext p_230322_4_) {
		return VoxelShapes.empty();
	}

	@OnlyIn(Dist.CLIENT)
	public float getAmbientOcclusionLightValue(BlockState p_220080_1_, IBlockReader p_220080_2_, BlockPos p_220080_3_) {
		return 1.0F;
	}

	public boolean propagatesSkylightDown(BlockState p_200123_1_, IBlockReader p_200123_2_, BlockPos p_200123_3_) {
		return true;
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
	public void onBlockExploded(BlockState state, World world, BlockPos pos, Explosion explosion) {
		super.onBlockExploded(state, world, pos ,explosion);
		onDestroy(world, pos);
	}

	@Override
	public void onPlayerDestroy(IWorld world, BlockPos pos, BlockState state) {
		super.onPlayerDestroy(world, pos, state);
		onDestroy((World) world, pos);
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
