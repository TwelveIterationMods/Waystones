package net.blay09.mods.waystones.block;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.IAttunementItem;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.api.WaystoneOrigin;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.blay09.mods.waystones.core.*;
import net.blay09.mods.waystones.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public abstract class WaystoneBlockBase extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<WaystoneOrigin> ORIGIN = EnumProperty.create("origin", WaystoneOrigin.class);

    public WaystoneBlockBase(Properties properties) {
        super(properties.pushReaction(PushReaction.BLOCK));
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false).setValue(ORIGIN, WaystoneOrigin.UNKNOWN));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState directionState, LevelAccessor world, BlockPos pos, BlockPos directionPos) {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }

        if (isDoubleBlock(state)) {
            DoubleBlockHalf half = state.getValue(HALF);
            if ((direction.getAxis() != Direction.Axis.Y) || ((half == DoubleBlockHalf.LOWER) != (direction == Direction.UP)) || ((directionState.getBlock() == this) && (directionState.getValue(
                    HALF) != half))) {
                if ((half != DoubleBlockHalf.LOWER) || (direction != Direction.DOWN) || state.canSurvive(world, pos)) {
                    return state;
                }
            }

            return Blocks.AIR.defaultBlockState();
        }

        return state;
    }

    @Override
    public void playerDestroy(Level world, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
        if (isDoubleBlock(state)) {
            super.playerDestroy(world, player, pos, Blocks.AIR.defaultBlockState(), blockEntity, stack);
        } else {
            super.playerDestroy(world, player, pos, state, blockEntity, stack);
        }
    }

    private boolean isDoubleBlock(BlockState state) {
        return state.hasProperty(HALF);
    }

    protected boolean canSilkTouch() {
        return false;
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        boolean isDoubleBlock = isDoubleBlock(state);
        DoubleBlockHalf half = isDoubleBlock ? state.getValue(HALF) : null;
        BlockPos offset = half == DoubleBlockHalf.LOWER ? pos.above() : pos.below();
        BlockEntity offsetTileEntity = isDoubleBlock ? world.getBlockEntity(offset) : null;

        boolean hasSilkTouch = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, player) > 0;
        if (hasSilkTouch && canSilkTouch()) {
            if (blockEntity instanceof WaystoneBlockEntityBase) {
                ((WaystoneBlockEntityBase) blockEntity).setSilkTouched(true);
            }
            if (isDoubleBlock && offsetTileEntity instanceof WaystoneBlockEntityBase) {
                ((WaystoneBlockEntityBase) offsetTileEntity).setSilkTouched(true);
            }
        }

        if (isDoubleBlock) {
            BlockState offsetState = world.getBlockState(offset);
            if (offsetState.getBlock() == this && offsetState.getValue(HALF) != half) {
                world.destroyBlock(half == DoubleBlockHalf.LOWER ? pos : offset, false, player);
                if (!world.isClientSide && !player.getAbilities().instabuild) {
                    dropResources(state, world, pos, blockEntity, player, player.getMainHandItem());
                    dropResources(offsetState, world, offset, offsetTileEntity, player, player.getMainHandItem());
                }
            }
        }

        if (blockEntity instanceof WaystoneBlockEntityBase waystoneBlockEntity && !player.getAbilities().instabuild) {
            if (waystoneBlockEntity.isCompletedFirstAttunement()) {
                for (int i = 0; i < waystoneBlockEntity.getContainerSize(); i++) {
                    ItemStack itemStack = waystoneBlockEntity.getItem(i);

                    // If not silk touching, don't bother dropping shards attuned to this waystone, since the waystone is gonna die anyways
                    if (!hasSilkTouch && itemStack.getItem() == ModItems.attunedShard) {
                        IWaystone waystoneAttunedTo = ((IAttunementItem) ModItems.attunedShard).getWaystoneAttunedTo(world.getServer(), itemStack);
                        if (waystoneAttunedTo != null && waystoneAttunedTo.getWaystoneUid().equals(waystoneBlockEntity.getWaystone().getWaystoneUid())) {
                            continue;
                        }
                    }

                    popResource(world, pos, itemStack);
                }
            }
        }

        super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED, ORIGIN);
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter world, BlockPos pos) {
        if (!WaystonePermissionManager.mayBreakWaystone(player, world, pos)) {
            return -1f;
        }

        return super.getDestroyProgress(state, player, world, pos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        if (!isDoubleBlock(state)) {
            return true;
        }

        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return true;
        }

        BlockState below = world.getBlockState(pos.below());
        return below.getBlock() == this && below.getValue(HALF) == DoubleBlockHalf.LOWER;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (!WaystonePermissionManager.mayPlaceWaystone(context.getPlayer())) {
            return null;
        }

        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        FluidState fluidState = world.getFluidState(pos);
        if (pos.getY() < world.getHeight() - 1) {
            if (world.getBlockState(pos.above()).canBeReplaced(context)) {
                return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite())
                        .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
            }
        }

        return null;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    protected void notifyObserversOfAction(Level world, BlockPos pos) {
        if (!world.isClientSide) {
            for (Direction direction : Direction.values()) {
                BlockPos offset = pos.relative(direction);
                BlockState neighbourState = world.getBlockState(offset);
                Block neighbourBlock = neighbourState.getBlock();
                if (neighbourBlock instanceof ObserverBlock && neighbourState.getValue(ObserverBlock.FACING) == direction.getOpposite()) {
                    if (!world.getBlockTicks().hasScheduledTick(offset, neighbourBlock)) {
                        world.scheduleTick(offset, neighbourBlock, 2);
                    }
                }
            }
        }
    }

    @Nullable
    protected InteractionResult handleEditActions(Level world, Player player, WaystoneBlockEntityBase tileEntity, IWaystone waystone) {
        if (player.isShiftKeyDown()) {
            WaystoneEditPermissions result = WaystonePermissionManager.mayEditWaystone(player, world, waystone);
            if (result != WaystoneEditPermissions.ALLOW) {
                if (result.getLangKey() != null) {
                    var chatComponent = Component.translatable(result.getLangKey());
                    chatComponent.withStyle(ChatFormatting.RED);
                    player.displayClientMessage(chatComponent, true);
                }
                return InteractionResult.SUCCESS;
            }

            if (!world.isClientSide) {
                MenuProvider settingsContainerProvider = tileEntity.getSettingsMenuProvider();
                if (settingsContainerProvider != null) {
                    Balm.getNetworking().openGui(player, settingsContainerProvider);
                }
            }
            return InteractionResult.SUCCESS;
        }

        return null;
    }

    protected boolean shouldOpenMenuWhenPlaced() {
        return true;
    }

    @Nullable
    protected InteractionResult handleDebugActions(Level world, Player player, InteractionHand hand, WaystoneBlockEntityBase tileEntity) {
        if (player.getAbilities().instabuild) {
            ItemStack heldItem = player.getItemInHand(hand);
            if (heldItem.getItem() == Items.BAMBOO) {
                if (!world.isClientSide) {
                    tileEntity.uninitializeWaystone();
                    player.displayClientMessage(Component.literal("Waystone was successfully reset - it will re-initialize once it is next loaded."), false);
                }
                return InteractionResult.SUCCESS;
            } else if (heldItem.getItem() == Items.STICK) {
                if (!world.isClientSide) {
                    player.displayClientMessage(Component.literal("Server UUID: " + tileEntity.getWaystone().getWaystoneUid()), false);
                }
                if (world.isClientSide) {
                    player.displayClientMessage(Component.literal("Client UUID: " + tileEntity.getWaystone().getWaystoneUid()), false);
                }
                return InteractionResult.SUCCESS;
            }
        }

        return null;
    }

    @Nullable
    protected InteractionResult handleActivation(Level world, BlockPos pos, Player player, WaystoneBlockEntityBase tileEntity, IWaystone waystone) {
        return null;
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            final var blockEntity = world.getBlockEntity(pos);
            final var waystone = ((WaystoneBlockEntityBase) blockEntity).getWaystone();
            final var wasNotSilkTouched = blockEntity instanceof WaystoneBlockEntityBase && (!canSilkTouch() || !((WaystoneBlockEntityBase) blockEntity).isSilkTouched());
            if (wasNotSilkTouched) {
                WaystoneManager.get(world.getServer()).removeWaystone(waystone);
                PlayerWaystoneManager.removeKnownWaystone(world.getServer(), waystone);
            }
            WaystoneSyncManager.sendWaystoneRemovalToAll(world.getServer(), waystone, wasNotSilkTouched);
        }

        super.onRemove(state, world, pos, newState, isMoving);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, world, list, flag);

        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("UUID", IntArrayTag.TAG_INT_ARRAY)) {
            WaystoneProxy waystone = new WaystoneProxy(null, NbtUtils.loadUUID(Objects.requireNonNull(tag.get("UUID"))));
            if (waystone.isValid()) {
                addWaystoneNameToTooltip(list, waystone);
            }
        }
    }

    protected void addWaystoneNameToTooltip(List<Component> tooltip, WaystoneProxy waystone) {
        var component = Component.literal(waystone.getName());
        component.withStyle(ChatFormatting.AQUA);
        tooltip.add(component);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockHitResult) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof WaystoneBlockEntityBase) {
            WaystoneBlockEntityBase waystoneTileEntity = (WaystoneBlockEntityBase) blockEntity;
            InteractionResult result = handleDebugActions(world, player, hand, waystoneTileEntity);
            if (result != null) {
                return result;
            }

            IWaystone waystone = waystoneTileEntity.getWaystone();
            result = handleEditActions(world, player, waystoneTileEntity, waystone);
            if (result != null) {
                return result;
            }

            result = handleActivation(world, pos, player, waystoneTileEntity, waystone);
            if (result != null) {
                return result;
            }
        }

        return InteractionResult.FAIL;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        BlockPos posAbove = pos.above();
        boolean isDoubleBlock = isDoubleBlock(state);
        if (isDoubleBlock) {
            FluidState fluidStateAbove = world.getFluidState(posAbove);
            world.setBlockAndUpdate(posAbove,
                    state.setValue(HALF, DoubleBlockHalf.UPPER)
                            .setValue(WATERLOGGED, fluidStateAbove.getType() == Fluids.WATER)
                            .setValue(ORIGIN, WaystoneOrigin.PLAYER));
        }

        if (blockEntity instanceof WaystoneBlockEntityBase) {
            if (!world.isClientSide) {
                CompoundTag tag = stack.getTag();
                WaystoneProxy existingWaystone = null;
                if (tag != null && tag.contains("UUID", IntArrayTag.TAG_INT_ARRAY)) {
                    existingWaystone = new WaystoneProxy(world.getServer(), NbtUtils.loadUUID(Objects.requireNonNull(tag.get("UUID"))));
                }

                if (existingWaystone != null && existingWaystone.isValid() && existingWaystone.getBackingWaystone() instanceof Waystone) {
                    ((WaystoneBlockEntityBase) blockEntity).initializeFromExisting((ServerLevelAccessor) world,
                            ((Waystone) existingWaystone.getBackingWaystone()),
                            stack);
                } else {
                    ((WaystoneBlockEntityBase) blockEntity).initializeWaystone((ServerLevelAccessor) world, placer, WaystoneOrigin.PLAYER);
                }

                if (isDoubleBlock) {
                    BlockEntity waystoneEntityAbove = world.getBlockEntity(posAbove);
                    if (waystoneEntityAbove instanceof WaystoneBlockEntityBase) {
                        ((WaystoneBlockEntityBase) waystoneEntityAbove).initializeFromBase(((WaystoneBlockEntityBase) blockEntity));
                    }
                }
            }

            if (placer instanceof Player) {
                IWaystone waystone = ((WaystoneBlockEntityBase) blockEntity).getWaystone();
                PlayerWaystoneManager.activateWaystone(((Player) placer), waystone);

                if (!world.isClientSide) {
                    WaystoneSyncManager.sendActivatedWaystones(((Player) placer));
                }
            }

            // Open settings screen on placement since people don't realize you can shift-click waystones to edit them
            if (!world.isClientSide && placer instanceof ServerPlayer) {
                final ServerPlayer player = (ServerPlayer) placer;
                final WaystoneBlockEntityBase waystoneTileEntity = (WaystoneBlockEntityBase) blockEntity;
                WaystoneEditPermissions result = WaystonePermissionManager.mayEditWaystone(player, world, waystoneTileEntity.getWaystone());
                if (result == WaystoneEditPermissions.ALLOW) {
                    MenuProvider settingsContainerProvider = waystoneTileEntity.getSettingsMenuProvider();
                    if (settingsContainerProvider != null && shouldOpenMenuWhenPlaced()) {
                        Balm.getNetworking().openGui(player, settingsContainerProvider);
                    }
                }
            }
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    public BlockEntityType<? extends WaystoneBlockEntityBase> getTickingBlockEntityType() {
        return null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        final var tickingBlockEntityType = getTickingBlockEntityType();
        if (tickingBlockEntityType == null) {
            return null;
        }
        return world.isClientSide ? null : createTickerHelper(type,
                tickingBlockEntityType,
                (level, pos, state2, blockEntity) -> blockEntity.serverTick());
    }
}
