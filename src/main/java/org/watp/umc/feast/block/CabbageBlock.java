package org.watp.umc.feast.block;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.RavagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.watp.umc.feast.Feast;

import java.util.Random;
import java.util.Set;

public class CabbageBlock extends CropsBlock implements IGenBlock {
	private static final IntegerProperty AGE = BlockStateProperties.AGE_0_3;
	private static final VoxelShape[] SHAPE_BY_AGE=new VoxelShape[]{};

	public CabbageBlock() {
		super(Properties.create(Material.GOURD));
		this.setDefaultState(this.getStateContainer().getBaseState().with(this.getAgeProperty(), Integer.valueOf(3)));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(AGE);
	}

	@Override
	public Set<String> genInBiomes() {
		return Sets.newHashSet("minecraft:plains",
				"minecraft:sunflower_plains");
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE_BY_AGE[state.get(this.getAgeProperty())];
	}

	@Override
	public IntegerProperty getAgeProperty() {
		return this.AGE;
	}

	@Override
	public int getMaxAge() {
		return 3;
	}

	@Override
	protected int getAge(BlockState state) {
		return state.get(this.getAgeProperty());
	}

	@Override
	public BlockState withAge(int age) {
		return this.getDefaultState().with(this.getAgeProperty(), Integer.valueOf(age));
	}

	public boolean isMaxAge(BlockState state) {
		return state.get(this.getAgeProperty()) >= this.getMaxAge();
	}

	@Override
	public boolean ticksRandomly(BlockState state) {
		return !this.isMaxAge(state);
	}

	@Override
	public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
      if (!worldIn.isAreaLoaded(pos, 1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light
      if (worldIn.getLightSubtracted(pos, 0) >= 9) {
         int i = this.getAge(state);
         if (i < this.getMaxAge()) {
            float f = getGrowthChance(this, worldIn, pos);
            if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state, random.nextInt((int)(25.0F / f) + 1) == 0)) {
               worldIn.setBlockState(pos, this.withAge(i + 1), 2);
               net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state);
            }
         }
      }

   }

	public void grow(World worldIn, BlockPos pos, BlockState state) {
		int i = this.getAge(state) + this.getBonemealAgeIncrease(worldIn);
		int j = this.getMaxAge();
		if (i > j) {
			i = j;
		}

		worldIn.setBlockState(pos, this.withAge(i), 2);
	}

	protected int getBonemealAgeIncrease(World worldIn) {
		return MathHelper.nextInt(worldIn.rand, 2, 5);
	}

	protected static float getGrowthChance(Block blockIn, IBlockReader world, BlockPos pos) {
		float f = 1.0F;
		BlockPos blockpos = pos.down();

		for(int i = -1; i <= 1; ++i) {
			for(int j = -1; j <= 1; ++j) {
				float f1 = 0.0F;
				BlockState blockstate = world.getBlockState(blockpos.add(i, 0, j));
				if (blockstate.canSustainPlant(world, blockpos.add(i, 0, j), net.minecraft.util.Direction.UP, (net.minecraftforge.common.IPlantable) blockIn)) {
					f1 = 1.0F;
					if (blockstate.isFertile(world, pos.add(i, 0, j))) {
						f1 = 3.0F;
					}
				}

				if (i != 0 || j != 0) {
					f1 /= 4.0F;
				}

				f += f1;
			}
		}

		BlockPos blockpos1 = pos.north();
		BlockPos blockpos2 = pos.south();
		BlockPos blockpos3 = pos.west();
		BlockPos blockpos4 = pos.east();
		boolean flag = blockIn == world.getBlockState(blockpos3).getBlock() || blockIn == world.getBlockState(blockpos4).getBlock();
		boolean flag1 = blockIn == world.getBlockState(blockpos1).getBlock() || blockIn == world.getBlockState(blockpos2).getBlock();
		if (flag && flag1) {
			f /= 2.0F;
		} else {
			boolean flag2 = blockIn == world.getBlockState(blockpos3.north()).getBlock() || blockIn == world.getBlockState(blockpos4.north()).getBlock() || blockIn == world.getBlockState(blockpos4.south()).getBlock() || blockIn == world.getBlockState(blockpos3.south()).getBlock();
			if (flag2) {
				f /= 2.0F;
			}
		}

		return f;
	}

	public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos) {
		return (world.getLightSubtracted(pos, 0) >= 8 || world.canSeeSky(pos)) && super.isValidPosition(state, world, pos);
	}

	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entityIn) {
		if (entityIn instanceof RavagerEntity && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(world, entityIn)) {
			world.destroyBlock(pos, true, entityIn);
		}

		super.onEntityCollision(state, world, pos, entityIn);
	}

	protected IItemProvider getSeedsItem() {
		return Feast.Items.CABBAGE_SEEDS;
	}

	public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
		return new ItemStack(this.getSeedsItem());
	}

	@Override
	public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
		return !this.isMaxAge(state);
	}

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(ServerWorld world, Random rand, BlockPos pos, BlockState state) {
		this.grow(world, pos, state);
	}
}
