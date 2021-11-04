package net.blay09.mods.waystones.block;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.core.*;
import net.blay09.mods.waystones.tileentity.WaystoneTileEntity;
import net.blay09.mods.waystones.tileentity.WaystoneTileEntityBase;
import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
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

    public WaystoneBlock() {
        setDefaultState(stateContainer.getBaseState().with(HALF, DoubleBlockHalf.LOWER).with(WATERLOGGED, false));
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
    protected boolean canSilkTouch() {
        return true;
    }

    @Override
    protected void handleActivation(World world, BlockPos pos, PlayerEntity player, WaystoneTileEntityBase tileEntity, IWaystone waystone) {
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

                WaystoneSyncManager.sendActivatedWaystones(player);
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

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(HALF);
    }

}
