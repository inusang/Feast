package org.watp.umc.feast.block;

import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.*;
import net.minecraft.world.Explosion;
import net.minecraft.world.IWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.watp.umc.feast.network.NetWorking;
import org.watp.umc.feast.network.PacketDMStatS2C;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Random;

public class DairyMachineBlock extends DestroyedGuiAutoCloseBlock {
	protected static final DirectionProperty FACING=HorizontalBlock.HORIZONTAL_FACING;
	public static final BooleanProperty WORKING=BooleanProperty.create("working");

	public DairyMachineBlock() {
		super(Properties.create(Material.IRON).hardnessAndResistance(15f,4f).harvestTool(ToolType.PICKAXE).harvestLevel(1).notSolid());
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(WORKING,false));
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(FACING,WORKING);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
	}

	public boolean propagatesSkylightDown(BlockState state, IBlockReader world, BlockPos pos) {
		return true;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		if (state.get(WORKING) && rand.nextDouble() > 0.5d) {
			double d0 = (double)pos.getX() + 0.5;
			double d1 = (double)pos.getY() + 1;
			double d2 = (double)pos.getZ() + 0.5;
			double d3 = rand.nextDouble() * 6.0d / 16.0d;
			world.addParticle(ParticleTypes.BUBBLE_COLUMN_UP, d0 , d1 + d3 , d2 , 0.0d, 0.0d, 0.0d);
		}
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
		if (!world.isRemote()) {
			final DairyMachineTileEntity te=(DairyMachineTileEntity) world.getTileEntity(pos);
			if (te!=null) {
				NetWorking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player)
						,new PacketDMStatS2C(pos, te.isOperable(), te.getProgress(), te.getProgressVisible(), te.getProductionTarget()));
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
