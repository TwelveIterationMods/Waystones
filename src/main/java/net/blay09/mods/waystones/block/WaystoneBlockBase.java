package net.blay09.mods.waystones.block;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.core.*;
import net.blay09.mods.waystones.tileentity.WaystoneTileEntityBase;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.*;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public abstract class WaystoneBlockBase extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public WaystoneBlockBase() {
        super(AbstractBlock.Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(5f, 2000f));
        this.setDefaultState(this.stateContainer.getBaseState().with(WATERLOGGED, false));
    }

    @Override
    public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState neighbor, IWorld world, BlockPos pos, BlockPos offset) {
        if (state.get(WATERLOGGED)) {
            world.getPendingFluidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        if (isDoubleBlock(state)) {
            DoubleBlockHalf half = state.get(HALF);
            if ((facing.getAxis() != Direction.Axis.Y) || ((half == DoubleBlockHalf.LOWER) != (facing == Direction.UP)) || ((neighbor.getBlock() == this) && (neighbor.get(HALF) != half))) {
                if ((half != DoubleBlockHalf.LOWER) || (facing != Direction.DOWN) || state.isValidPosition(world, pos)) {
                    return state;
                }
            }

            return Blocks.AIR.getDefaultState();
        }

        return state;
    }


    @Override
    public void harvestBlock(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
        if (isDoubleBlock(state)) {
            super.harvestBlock(world, player, pos, Blocks.AIR.getDefaultState(), te, stack);
        } else {
            super.harvestBlock(world, player, pos, state, te, stack);
        }
    }

    private boolean isDoubleBlock(BlockState state) {
        return state.hasProperty(HALF);
    }

    protected boolean canSilkTouch() {
        return false;
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        TileEntity tileEntity = world.getTileEntity(pos);

        boolean isDoubleBlock = isDoubleBlock(state);
        DoubleBlockHalf half = isDoubleBlock ? state.get(HALF) : null;
        BlockPos offset = half == DoubleBlockHalf.LOWER ? pos.up() : pos.down();
        TileEntity offsetTileEntity = isDoubleBlock ? world.getTileEntity(offset) : null;

        boolean hasSilkTouch = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.SILK_TOUCH, player) > 0;
        if (hasSilkTouch && canSilkTouch()) {
            if (tileEntity instanceof WaystoneTileEntityBase) {
                ((WaystoneTileEntityBase) tileEntity).setSilkTouched(true);
            }
            if (isDoubleBlock && offsetTileEntity instanceof WaystoneTileEntityBase) {
                ((WaystoneTileEntityBase) offsetTileEntity).setSilkTouched(true);
            }
        }

        if (isDoubleBlock) {
            BlockState offsetState = world.getBlockState(offset);
            if (offsetState.getBlock() == this && offsetState.get(HALF) != half) {
                world.destroyBlock(half == DoubleBlockHalf.LOWER ? pos : offset, false, player);
                if (!world.isRemote && !player.abilities.isCreativeMode) {
                    spawnDrops(state, world, pos, tileEntity, player, player.getHeldItemMainhand());
                    spawnDrops(offsetState, world, offset, offsetTileEntity, player, player.getHeldItemMainhand());
                }
            }
        }

        super.onBlockHarvested(world, pos, state, player);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
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
        if (!isDoubleBlock(state)) {
            return true;
        }

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

    @Nullable
    protected ActionResultType handleEditActions(World world, PlayerEntity player, WaystoneTileEntityBase tileEntity, IWaystone waystone) {
        if (player.isSneaking()) {
            WaystoneEditPermissions result = PlayerWaystoneManager.mayEditWaystone(player, world, waystone);
            if (result != WaystoneEditPermissions.ALLOW) {
                if (result.getLangKey() != null) {
                    TranslationTextComponent chatComponent = new TranslationTextComponent(result.getLangKey());
                    chatComponent.mergeStyle(TextFormatting.RED);
                    player.sendStatusMessage(chatComponent, true);
                }
                return ActionResultType.SUCCESS;
            }

            if (!world.isRemote) {
                INamedContainerProvider settingsContainerProvider = tileEntity.getWaystoneSettingsContainerProvider();
                if (settingsContainerProvider != null) {
                    NetworkHooks.openGui(((ServerPlayerEntity) player), settingsContainerProvider, buf -> Waystone.write(buf, tileEntity.getWaystone()));
                }
            }
            return ActionResultType.SUCCESS;
        }

        return null;
    }

    @Nullable
    protected ActionResultType handleDebugActions(World world, PlayerEntity player, Hand hand, WaystoneTileEntityBase tileEntity) {
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

        return null;
    }

    protected void handleActivation(World world, BlockPos pos, PlayerEntity player, WaystoneTileEntityBase tileEntity, IWaystone waystone) {
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.isIn(newState.getBlock())) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof WaystoneTileEntityBase && (!canSilkTouch() || !((WaystoneTileEntityBase) tileEntity).isSilkTouched())) {
                IWaystone waystone = ((WaystoneTileEntityBase) tileEntity).getWaystone();
                WaystoneManager.get().removeWaystone(waystone);
                PlayerWaystoneManager.removeKnownWaystone(waystone);
            }
        }

        super.onReplaced(state, world, pos, newState, isMoving);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        CompoundNBT tagCompound = stack.getTag();
        if (tagCompound != null && tagCompound.contains("UUID", Constants.NBT.TAG_INT_ARRAY)) {
            WaystoneProxy waystone = new WaystoneProxy(NBTUtil.readUniqueId(Objects.requireNonNull(tagCompound.get("UUID"))));
            if (waystone.isValid()) {
                addWaystoneNameToTooltip(tooltip, waystone);
            }
        }
    }

    protected void addWaystoneNameToTooltip(List<ITextComponent> tooltip, WaystoneProxy waystone) {
        StringTextComponent component = new StringTextComponent(waystone.getName());
        component.mergeStyle(TextFormatting.AQUA);
        tooltip.add(component);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof WaystoneTileEntityBase) {
            WaystoneTileEntityBase waystoneTileEntity = (WaystoneTileEntityBase) tileEntity;
            ActionResultType result = handleDebugActions(world, player, hand, waystoneTileEntity);
            if (result != null) {
                return result;
            }

            IWaystone waystone = waystoneTileEntity.getWaystone();
            result = handleEditActions(world, player, waystoneTileEntity, waystone);
            if (result != null) {
                return result;
            }

            handleActivation(world, pos, player, waystoneTileEntity, waystone);

            return ActionResultType.SUCCESS;
        }

        return ActionResultType.FAIL;
    }


    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        TileEntity tileEntity = world.getTileEntity(pos);

        BlockPos posAbove = pos.up();
        boolean isDoubleBlock = isDoubleBlock(state);
        if (isDoubleBlock) {
            FluidState fluidStateAbove = world.getFluidState(posAbove);
            world.setBlockState(posAbove, state.with(HALF, DoubleBlockHalf.UPPER).with(WATERLOGGED, fluidStateAbove.getFluid() == Fluids.WATER));
        }

        if (tileEntity instanceof WaystoneTileEntityBase) {
            if (!world.isRemote) {
                CompoundNBT tag = stack.getTag();
                WaystoneProxy existingWaystone = null;
                if (tag != null && tag.contains("UUID", Constants.NBT.TAG_INT_ARRAY)) {
                    existingWaystone = new WaystoneProxy(NBTUtil.readUniqueId(Objects.requireNonNull(tag.get("UUID"))));
                }

                if (existingWaystone != null && existingWaystone.isValid() && existingWaystone.getBackingWaystone() instanceof Waystone) {
                    ((WaystoneTileEntityBase) tileEntity).initializeFromExisting((IServerWorld) world, ((Waystone) existingWaystone.getBackingWaystone()), stack);
                } else {
                    ((WaystoneTileEntityBase) tileEntity).initializeWaystone((IServerWorld) world, placer, false);
                }

                if (isDoubleBlock) {
                    TileEntity waystoneTileEntityAbove = world.getTileEntity(posAbove);
                    if (waystoneTileEntityAbove instanceof WaystoneTileEntityBase) {
                        ((WaystoneTileEntityBase) waystoneTileEntityAbove).initializeFromBase(((WaystoneTileEntityBase) tileEntity));
                    }
                }
            }

            if (placer instanceof PlayerEntity) {
                IWaystone waystone = ((WaystoneTileEntityBase) tileEntity).getWaystone();
                PlayerWaystoneManager.activateWaystone(((PlayerEntity) placer), waystone);

                if (!world.isRemote) {
                    WaystoneSyncManager.sendActivatedWaystones(((PlayerEntity) placer));
                }
            }

            // Open settings screen on placement since people don't realize you can shift-click waystones to edit them
            if (!world.isRemote && placer instanceof ServerPlayerEntity) {
                final ServerPlayerEntity player = (ServerPlayerEntity) placer;
                final WaystoneTileEntityBase waystoneTileEntity = (WaystoneTileEntityBase) tileEntity;
                WaystoneEditPermissions result = PlayerWaystoneManager.mayEditWaystone(player, world, waystoneTileEntity.getWaystone());
                if (result == WaystoneEditPermissions.ALLOW) {
                    INamedContainerProvider settingsContainerProvider = waystoneTileEntity.getWaystoneSettingsContainerProvider();
                    if (settingsContainerProvider != null) {
                        NetworkHooks.openGui(player, settingsContainerProvider, buf -> Waystone.write(buf, waystoneTileEntity.getWaystone()));
                    }
                }
            }
        }
    }

    @Nullable
    @Override
    public PathNodeType getAiPathNodeType(BlockState state, IBlockReader world, BlockPos pos, @Nullable MobEntity entity) {
        return PathNodeType.BLOCKED;
    }

}
