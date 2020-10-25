package org.watp.umc.feast.block;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IWorld;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.watp.umc.feast.network.NetWorking;
import org.watp.umc.feast.network.PacketOvenStatSync;
import org.watp.umc.feast.tileentity.OvenTileEntity;
import org.watp.umc.feast.tileentity.OvenTileEntity.VisibleIntValueType;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;

public class OvenBlock extends DestroyedGuiAutoCloseBlock {
	protected static final DirectionProperty FACING=HorizontalBlock.HORIZONTAL_FACING;
	public static final IntegerProperty OVEN_STAT=IntegerProperty.create("oven_stat",0,3);			//0:off 1:standby 2:on 3:danger
	
	public OvenBlock() {
		super(Properties.create(Material.IRON).hardnessAndResistance(3f,2f).harvestTool(ToolType.PICKAXE).harvestLevel(1).
				func_235838_a_(bs->0));		//control the lightValue
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(OVEN_STAT,0));
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
	}
	
	@Override
	public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
		int ovenStat=state.func_235903_d_(OVEN_STAT).orElse(0);
		if (ovenStat==1) return 5;
		else if (ovenStat==2) return 14;
		else if (ovenStat==3) return 15;
		else return 0;
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(FACING,OVEN_STAT);
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player,
			Hand hand, BlockRayTraceResult hit) {
		if (!world.isRemote()) {
			if (!(Math.sqrt(player.getDistanceSq(pos.getX(),pos.getY(),pos.getZ()))>4 ||
					player.getHorizontalFacing().getOpposite()!=state.get(FACING))) {
				final OvenTileEntity te=(OvenTileEntity) world.getTileEntity(pos);
				if (te!=null) {
					NetWorking.INSTANCE.send(PacketDistributor.SERVER.noArg()
							,new PacketOvenStatSync(pos,te.getIntVisibleValue(VisibleIntValueType.PROGRESS),te.getIntVisibleValue(VisibleIntValueType.TEMPERATURE),
									te.getIntVisibleValue(VisibleIntValueType.REMAINING_ENERGY),te.getIntVisibleValue(VisibleIntValueType.REMAINING_COOLING),te.getOpen()));
					te.openGUI((ServerPlayerEntity)player);
				}
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
		return new OvenTileEntity();
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

}