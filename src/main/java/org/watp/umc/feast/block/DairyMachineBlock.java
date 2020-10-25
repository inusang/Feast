package org.watp.umc.feast.block;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.Explosion;
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

import javax.annotation.Nullable;

public class DairyMachineBlock extends DestroiedGuiAutoCloseBlock {
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
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack itemStack) {
		super.onBlockPlacedBy(world, pos, state, entity, itemStack);
		onDestroy(world, pos);
	}

	@Override
	public void onExplosionDestroy(World world, BlockPos pos, Explosion explosion) {
		super.onExplosionDestroy(world, pos, explosion);
		onDestroy(world, pos);
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
