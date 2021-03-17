package net.blay09.mods.waystones.block;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.core.*;
import net.blay09.mods.waystones.tileentity.WaystoneTileEntity;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.piglin.PiglinTasks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class WaystoneBlock extends WaystoneBlockBase {

    private static final VoxelShape LOWER_SHAPE = VoxelShapes.or(
            makeCuboidShape(0.0, 0.0, 0.0, 16.0, 3.0, 16.0),
            makeCuboidShape(1.0, 3.0, 1.0, 15.0, 7.0, 15.0),
            makeCuboidShape(2.0, 7.0, 2.0, 14.0, 9.0, 14.0),
            makeCuboidShape(3.0, 9.0, 3.0, 13.0, 16.0, 13.0)
    ).simplify();

    private static final VoxelShape UPPER_SHAPE = VoxelShapes.or(
            makeCuboidShape(3.0, 0.0, 3.0, 13.0, 8.0, 13.0),
            makeCuboidShape(2.0, 8.0, 2.0, 14.0, 10.0, 14.0),
            makeCuboidShape(1.0, 10.0, 1.0, 15.0, 12.0, 15.0),
            makeCuboidShape(3.0, 12.0, 3.0, 13.0, 14.0, 13.0),
            makeCuboidShape(4.0, 14.0, 4.0, 12.0, 16.0, 12.0)
    ).simplify();

    @Override
    public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        DoubleBlockHalf half = state.get(HALF);
        BlockPos offset = half == DoubleBlockHalf.LOWER ? pos.up() : pos.down();
        TileEntity tileEntity = world.getTileEntity(pos);
        TileEntity offsetTileEntity = world.getTileEntity(offset);

        boolean hasSilkTouch = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.SILK_TOUCH, player) > 0;
        if (hasSilkTouch) {
            if (tileEntity instanceof WaystoneTileEntity) {
                ((WaystoneTileEntity) tileEntity).setSilkTouched(true);
            }
            if (offsetTileEntity instanceof WaystoneTileEntity) {
                ((WaystoneTileEntity) offsetTileEntity).setSilkTouched(true);
            }
        }

        super.onBlockHarvested(world, pos, state, player);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return state.get(HALF) == DoubleBlockHalf.UPPER ? UPPER_SHAPE : LOWER_SHAPE;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new WaystoneTileEntity();
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        BlockPos posAbove = pos.up();
        FluidState fluidStateAbove = world.getFluidState(posAbove);
        world.setBlockState(posAbove, state.with(HALF, DoubleBlockHalf.UPPER).with(WATERLOGGED, fluidStateAbove.getFluid() == Fluids.WATER));

        TileEntity tileEntity = world.getTileEntity(pos);
        TileEntity waystoneTileEntityAbove = world.getTileEntity(posAbove);
        if (tileEntity instanceof WaystoneTileEntity) {
            if (!world.isRemote) {
                CompoundNBT tag = stack.getTag();
                WaystoneProxy existingWaystone = null;
                if (tag != null && tag.contains("UUID", Constants.NBT.TAG_INT_ARRAY)) {
                    existingWaystone = new WaystoneProxy(NBTUtil.readUniqueId(Objects.requireNonNull(tag.get("UUID"))));
                }

                if (existingWaystone != null && existingWaystone.isValid() && existingWaystone.getBackingWaystone() instanceof Waystone) {
                    ((WaystoneTileEntity) tileEntity).initializeFromExisting((IServerWorld) world, ((Waystone) existingWaystone.getBackingWaystone()));
                } else {
                    ((WaystoneTileEntity) tileEntity).initializeWaystone((IServerWorld) world, placer, false);
                }

                if (waystoneTileEntityAbove instanceof WaystoneTileEntity) {
                    ((WaystoneTileEntity) waystoneTileEntityAbove).initializeFromBase(((WaystoneTileEntity) tileEntity));
                }
            }

            if (placer instanceof PlayerEntity && waystoneTileEntityAbove instanceof WaystoneTileEntity) {
                IWaystone waystone = ((WaystoneTileEntity) waystoneTileEntityAbove).getWaystone();
                PlayerWaystoneManager.activateWaystone(((PlayerEntity) placer), waystone);

                if (!world.isRemote) {
                    WaystoneSyncManager.sendKnownWaystones(((PlayerEntity) placer));
                }
            }

            // Open settings screen on placement since people don't realize you can shift-click waystones to edit them
            if (!world.isRemote && placer instanceof ServerPlayerEntity) {
                final ServerPlayerEntity player = (ServerPlayerEntity) placer;
                final WaystoneTileEntity waystoneTileEntity = (WaystoneTileEntity) tileEntity;
                WaystoneEditPermissions result = PlayerWaystoneManager.mayEditWaystone(player, world, waystoneTileEntity.getWaystone());
                if (result == WaystoneEditPermissions.ALLOW) {
                    NetworkHooks.openGui(player, waystoneTileEntity.getWaystoneSettingsContainerProvider(), buf -> Waystone.write(buf, waystoneTileEntity.getWaystone()));
                }
            }
        }
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.isIn(newState.getBlock())) {
            WaystoneTileEntity tileEntity = (WaystoneTileEntity) world.getTileEntity(pos);
            if (tileEntity != null && !tileEntity.isSilkTouched()) {
                IWaystone waystone = tileEntity.getWaystone();
                WaystoneManager.get().removeWaystone(waystone);
                PlayerWaystoneManager.removeKnownWaystone(waystone);
            }
        }

        super.onReplaced(state, world, pos, newState, isMoving);
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
                NetworkHooks.openGui(((ServerPlayerEntity) player), tileEntity.getWaystoneSettingsContainerProvider(), buf -> Waystone.write(buf, tileEntity.getWaystone()));
            }
            return ActionResultType.SUCCESS;
        }

        boolean isActivated = PlayerWaystoneManager.isWaystoneActivated(player, waystone);
        if (isActivated) {
            if (!world.isRemote) {
                if (WaystonesConfig.COMMON.allowWaystoneToWaystoneTeleport.get()) {
                    NetworkHooks.openGui(((ServerPlayerEntity) player), tileEntity.getWaystoneSelectionContainerProvider(), it -> {
                        it.writeByte(WarpMode.WAYSTONE_TO_WAYSTONE.ordinal());
                        it.writeBlockPos(pos);
                    });
                } else {
                    player.sendStatusMessage(new TranslationTextComponent("chat.waystones.waystone_to_waystone_disabled"), true);
                }
            }
        } else {
            PlayerWaystoneManager.activateWaystone(player, waystone);

            if (!world.isRemote) {
                StringTextComponent nameComponent = new StringTextComponent(waystone.getName());
                nameComponent.mergeStyle(TextFormatting.WHITE);
                TranslationTextComponent chatComponent = new TranslationTextComponent("chat.waystones.waystone_activated", nameComponent);
                chatComponent.mergeStyle(TextFormatting.YELLOW);
                player.sendMessage(chatComponent, Util.DUMMY_UUID);

                WaystoneSyncManager.sendKnownWaystones(player);
            }

            notifyObserversOfAction(world, pos);

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

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        CompoundNBT tagCompound = stack.getTag();
        if (tagCompound != null && tagCompound.contains("UUID", Constants.NBT.TAG_INT_ARRAY)) {
            WaystoneProxy waystone = new WaystoneProxy(NBTUtil.readUniqueId(Objects.requireNonNull(tagCompound.get("UUID"))));
            if (waystone.isValid()) {
                StringTextComponent component = new StringTextComponent(waystone.getName());
                component.mergeStyle(TextFormatting.AQUA);
                tooltip.add(component);
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!WaystonesConfig.CLIENT.disableParticles.get() && random.nextFloat() < 0.75f) {
            TileEntity tileEntity = world.getTileEntity(pos);
            PlayerEntity player = Minecraft.getInstance().player;
            if (tileEntity instanceof WaystoneTileEntity && PlayerWaystoneManager.isWaystoneActivated(Objects.requireNonNull(player), ((WaystoneTileEntity) tileEntity).getWaystone())) {
                world.addParticle(ParticleTypes.PORTAL, pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 1.5, pos.getY() + 0.5, pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 1.5, 0, 0, 0);
                world.addParticle(ParticleTypes.ENCHANT, pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 1.5, pos.getY() + 0.5, pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 1.5, 0, 0, 0);
            }
        }
    }

}
