package net.blay09.mods.waystones.block;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.config.WaystoneConfig;
import net.blay09.mods.waystones.core.*;
import net.blay09.mods.waystones.tileentity.WaystoneTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.ObserverBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Random;

public class WaystoneBlock extends Block implements IWaterLoggable {

    // Precise selection box
    private static final VoxelShape WAYSTONE_UPPER = VoxelShapes.or(
      Block.makeCuboidShape(3, 0, 3, 13, 8, 13),
      Block.makeCuboidShape(2, 8, 2, 14, 10, 14),
      Block.makeCuboidShape(1, 10, 1, 15, 12, 15),
      Block.makeCuboidShape(3, 12, 3, 13, 14, 13),
      Block.makeCuboidShape(4, 14, 4, 12, 16, 12))
      .simplify();

    private static final VoxelShape WAYSTONE_LOWER = VoxelShapes.or(
      Block.makeCuboidShape(0, 0, 0, 16, 3, 16),
      Block.makeCuboidShape(1, 3, 1, 15, 7, 15),
      Block.makeCuboidShape(2, 7, 2, 14, 9, 14),
      Block.makeCuboidShape(3, 9, 3, 13, 16, 13))
      .simplify();

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public WaystoneBlock() {
        super(Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(5f, 2000f));
        this.setDefaultState(this.stateContainer.getBaseState().with(WaystoneBlock.WATERLOGGED, false));
    }

    @Override
    public VoxelShape getShape(BlockState p_196247_1_, IBlockReader p_196247_2_, BlockPos p_196247_3_, final ISelectionContext context) {
        final DoubleBlockHalf half = p_196247_1_.get(WaystoneBlock.HALF);
        if (half == DoubleBlockHalf.LOWER) return WaystoneBlock.WAYSTONE_LOWER;
        else return WaystoneBlock.WAYSTONE_UPPER;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(HALF);
        builder.add(WATERLOGGED);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new WaystoneTileEntity();
    }

    @Override
    public float getPlayerRelativeBlockHardness(BlockState state, PlayerEntity player, IBlockReader world, BlockPos pos) {
        if (!PlayerWaystoneManager.mayBreakWaystone(player, world, pos)) {
            return -1f;
        }

        return super.getPlayerRelativeBlockHardness(state, player, world, pos);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        if (!PlayerWaystoneManager.mayPlaceWaystone(context.getPlayer())) {
            return null;
        }

        final BlockPos pos = context.getPos();
        final IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());

        final BlockPos waystonePos = this.getWaystoneUpperPos(pos, context.getPlacementHorizontalFacing().getOpposite());
        if (pos.getY() < 255 && waystonePos.getY() < 255 && context.getWorld().getBlockState(pos.up()).isReplaceable(
          context)) return this.getDefaultState().with(FACING, context
          .getPlacementHorizontalFacing()).with(WaystoneBlock.HALF, DoubleBlockHalf.LOWER)
          .with(WaystoneBlock.WATERLOGGED, ifluidstate.isTagged(FluidTags.WATER) && ifluidstate.getLevel() == 8);
        else return null;
    }

    private BlockPos getWaystoneUpperPos(final BlockPos base, final Direction facing)
    {
        switch (facing)
        {
            default:
                return base.up();
        }
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {

        //Sets top state & checks for water
        final IFluidState fluidState = world.getFluidState(pos.up());
        if (placer != null)
        {
            world.setBlockState(pos.up(), state.with(WaystoneBlock.HALF, DoubleBlockHalf.UPPER).with(
              WaystoneBlock.WATERLOGGED, fluidState.getFluid() == Fluids.WATER), 1);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public IFluidState getFluidState(final BlockState state)
    {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        WaystoneTileEntity tileEntity = (WaystoneTileEntity) world.getTileEntity(pos);
        if (tileEntity != null) {
            IWaystone waystone = tileEntity.getWaystone();
            WaystoneManager.get().removeWaystone(waystone);
            PlayerWaystoneManager.removeKnownWaystone(waystone);
        }

        super.onReplaced(state, world, pos, newState, isMoving);

        // Also destroy the connect upper or lower waystone block
        final IFluidState fluidState = world.getFluidState(pos);
        if (world.getBlockState(pos.up()).getBlock() == this) {
            if (fluidState.getFluid() == Fluids.WATER) {
                world.setBlockState(pos, fluidState.getBlockState(), 35);
                world.removeBlock(pos.up(), false);
            } else world.removeBlock(pos.up(), false);
        } else if (world.getBlockState(pos.down()).getBlock() == this) {
            if (fluidState.getFluid() == Fluids.WATER) {
                world.setBlockState(pos, fluidState.getBlockState(), 35);
                world.removeBlock(pos.down(), false);
            } else world.removeBlock(pos.down(), false);
        }
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
        WaystoneTileEntity tileEntity = (WaystoneTileEntity) world.getTileEntity(pos);
        if (tileEntity == null) {
            return ActionResultType.FAIL;
        }

        if (player.abilities.isCreativeMode) {
            ItemStack heldItem = player.getHeldItem(hand);
            if (heldItem.getItem() == Items.BAMBOO) {
                if (!world.isRemote) {
                    tileEntity.uninitializeWaystone();
                    player.sendStatusMessage(new StringTextComponent("Waystone was successfully reset - it will re-initialize once it is next loaded."), false);
                }
                return ActionResultType.SUCCESS;
            } else if (heldItem.getItem() == Items.STICK) {
                if (!world.isRemote) {
                    player.sendStatusMessage(new StringTextComponent("Waystone UUID: " + tileEntity.getWaystone().getWaystoneUid()), false);
                }
                return ActionResultType.SUCCESS;
            }
        }

        IWaystone waystone = tileEntity.getWaystone();
        if (player.isShiftKeyDown()) {
            WaystoneEditPermissions result = PlayerWaystoneManager.mayEditWaystone(player, world, waystone);
            if (result != WaystoneEditPermissions.ALLOW) {
                if (result.getLangKey() != null) {
                    TranslationTextComponent chatComponent = new TranslationTextComponent(result.getLangKey());
                    chatComponent.getStyle().setColor(TextFormatting.RED);
                    player.sendStatusMessage(chatComponent, true);
                }
                return ActionResultType.SUCCESS;
            }

            if (!world.isRemote) {
                NetworkHooks.openGui(((ServerPlayerEntity) player), tileEntity.getWaystoneSettingsContainerProvider(), pos);
            }
            return ActionResultType.SUCCESS;
        }

        boolean isActivated = PlayerWaystoneManager.isWaystoneActivated(player, waystone);
        if (isActivated) {
            if (!world.isRemote) {
                NetworkHooks.openGui(((ServerPlayerEntity) player), tileEntity.getWaystoneSelectionContainerProvider(), it -> {
                    it.writeByte(WarpMode.WAYSTONE_TO_WAYSTONE.ordinal());
                    it.writeBlockPos(pos);
                });
            }
        } else {
            PlayerWaystoneManager.activateWaystone(player, waystone);

            if (!world.isRemote) {
                StringTextComponent nameComponent = new StringTextComponent(waystone.getName());
                nameComponent.getStyle().setColor(TextFormatting.WHITE);
                TranslationTextComponent chatComponent = new TranslationTextComponent("chat.waystones.waystone_activated", nameComponent);
                chatComponent.getStyle().setColor(TextFormatting.YELLOW);
                player.sendMessage(chatComponent);

                WaystoneSyncManager.sendKnownWaystones(player);
            }

            notifyObserversOfActivation(world, pos);

            if (world.isRemote) {
                Waystones.proxy.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, pos, 1f);
                for (int i = 0; i < 32; i++) {
                    world.addParticle(ParticleTypes.ENCHANT, pos.getX() + 0.5 + (world.rand.nextDouble() - 0.5) * 2, pos.getY() + 3, pos.getZ() + 0.5 + (world.rand.nextDouble() - 0.5) * 2, 0, -5, 0);
                    world.addParticle(ParticleTypes.ENCHANT, pos.getX() + 0.5 + (world.rand.nextDouble() - 0.5) * 2, pos.getY() + 4, pos.getZ() + 0.5 + (world.rand.nextDouble() - 0.5) * 2, 0, -5, 0);
                }
            }
        }

        return ActionResultType.SUCCESS;
    }

    private void notifyObserversOfActivation(World world, BlockPos pos) {
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

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!WaystoneConfig.CLIENT.disableParticles.get() && random.nextFloat() < 0.75f) {
            WaystoneTileEntity tileEntity = (WaystoneTileEntity) world.getTileEntity(pos);
            PlayerEntity player = Minecraft.getInstance().player;
            if (tileEntity != null && PlayerWaystoneManager.isWaystoneActivated(Objects.requireNonNull(player), tileEntity.getWaystone())) {
                world.addParticle(ParticleTypes.PORTAL, pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 1.5, pos.getY() + 0.5, pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 1.5, 0, 0, 0);
                world.addParticle(ParticleTypes.ENCHANT, pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 1.5, pos.getY() + 0.5, pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 1.5, 0, 0, 0);
            }
        }
    }

}
