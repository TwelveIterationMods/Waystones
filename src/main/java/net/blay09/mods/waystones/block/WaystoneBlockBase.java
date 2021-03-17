package net.blay09.mods.waystones.block;

import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.tileentity.WaystoneTileEntity;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class WaystoneBlockBase extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public WaystoneBlockBase() {
        super(AbstractBlock.Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(5f, 2000f));
        this.setDefaultState(this.stateContainer.getBaseState().with(HALF, DoubleBlockHalf.LOWER).with(WATERLOGGED, false));
    }

    @Override
    public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState neighbor, IWorld world, BlockPos pos, BlockPos offset) {
        if (state.get(WATERLOGGED)) {
            world.getPendingFluidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        DoubleBlockHalf half = state.get(HALF);
        if ((facing.getAxis() != Direction.Axis.Y) || ((half == DoubleBlockHalf.LOWER) != (facing == Direction.UP)) || ((neighbor.getBlock() == this) && (neighbor.get(HALF) != half))) {
            if ((half != DoubleBlockHalf.LOWER) || (facing != Direction.DOWN) || state.isValidPosition(world, pos)) {
                return state;
            }
        }

        return Blocks.AIR.getDefaultState();
    }

    @Override
    public void harvestBlock(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
        super.harvestBlock(world, player, pos, Blocks.AIR.getDefaultState(), te, stack);
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        DoubleBlockHalf half = state.get(HALF);
        BlockPos offset = half == DoubleBlockHalf.LOWER ? pos.up() : pos.down();
        TileEntity tileEntity = world.getTileEntity(pos);
        TileEntity offsetTileEntity = world.getTileEntity(offset);
        BlockState offsetState = world.getBlockState(offset);
        if (offsetState.getBlock() == this && offsetState.get(HALF) != half) {
            world.destroyBlock(half == DoubleBlockHalf.LOWER ? pos : offset, false, player);
            if (!world.isRemote && !player.abilities.isCreativeMode) {
                spawnDrops(state, world, pos, tileEntity, player, player.getHeldItemMainhand());
                spawnDrops(offsetState, world, offset, offsetTileEntity, player, player.getHeldItemMainhand());
            }
        }

        super.onBlockHarvested(world, pos, state, player);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF, WATERLOGGED);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public abstract TileEntity createTileEntity(BlockState state, IBlockReader world);

    @Override
    public float getPlayerRelativeBlockHardness(BlockState state, PlayerEntity player, IBlockReader world, BlockPos pos) {
        if (!PlayerWaystoneManager.mayBreakWaystone(player, world, pos)) {
            return -1f;
        }

        return super.getPlayerRelativeBlockHardness(state, player, world, pos);
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos) {
        if (state.get(HALF) == DoubleBlockHalf.LOWER) {
            return true;
        }

        BlockState below = world.getBlockState(pos.down());
        return below.getBlock() == this && below.get(HALF) == DoubleBlockHalf.LOWER;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        if (!PlayerWaystoneManager.mayPlaceWaystone(context.getPlayer())) {
            return null;
        }

        World world = context.getWorld();
        BlockPos pos = context.getPos();
        FluidState fluidState = world.getFluidState(pos);
        if (pos.getY() < world.getHeight() - 1) {
            if (world.getBlockState(pos.up()).isReplaceable(context)) {
                return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite())
                        .with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
            }
        }

        return null;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    @Override
    public PushReaction getPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    protected void notifyObserversOfAction(World world, BlockPos pos) {
        if (!world.isRemote) {
            for (Direction direction : Direction.values()) {
                BlockPos offset = pos.offset(direction);
                BlockState neighbourState = world.getBlockState(offset);
                Block neighbourBlock = neighbourState.getBlock();
                if (neighbourBlock instanceof ObserverBlock && neighbourState.get(ObserverBlock.FACING) == direction.getOpposite()) {
                    if (!world.getPendingBlockTicks().isTickScheduled(offset, neighbourBlock)) {
                        world.getPendingBlockTicks().scheduleTick(offset, neighbourBlock, 2);
                    }
                }
            }
        }
    }
}
