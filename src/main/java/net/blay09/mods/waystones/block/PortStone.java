package net.blay09.mods.waystones.block;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.container.WaystoneSelectionContainer;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WarpMode;
import net.blay09.mods.waystones.tileentity.WaystoneTileEntity;
import net.blay09.mods.waystones.tileentity.WaystoneTileEntityBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Random;

public class PortStone extends WaystoneBlockBase {

    private static final VoxelShape LOWER_SHAPE = VoxelShapes.or(
            makeCuboidShape(0.0, 0.0, 0.0, 16.0, 3.0, 16.0),
            makeCuboidShape(1.0, 3.0, 1.0, 15.0, 7.0, 15.0),
            makeCuboidShape(2.0, 7.0, 2.0, 14.0, 9.0, 14.0),
            makeCuboidShape(3.0, 9.0, 3.0, 13.0, 16.0, 13.0)
    ).simplify();

    private static final VoxelShape UPPER_SHAPE = VoxelShapes.or(
            makeCuboidShape(3.0, 0.0, 3.0, 13.0, 8.0, 13.0),
            makeCuboidShape(4.0, 8.0, 4.0, 12.0, 16.0, 12.0)
    ).simplify();

    public PortStone() {
        setDefaultState(stateContainer.getBaseState().with(HALF, DoubleBlockHalf.LOWER).with(WATERLOGGED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return state.get(HALF) == DoubleBlockHalf.UPPER ? UPPER_SHAPE : LOWER_SHAPE;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return null;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace) {
        handleActivation(world, pos, player, null, null);
        return ActionResultType.SUCCESS;
    }

    @Override
    protected void handleActivation(World world, BlockPos pos, PlayerEntity player, @Nullable WaystoneTileEntityBase tile, @Nullable IWaystone waystone) {
        if (!world.isRemote) {
            NetworkHooks.openGui(((ServerPlayerEntity) player), new INamedContainerProvider() {
                @Override
                public ITextComponent getDisplayName() {
                    return PortStone.this.getTranslatedName();
                }

                @Override
                public Container createMenu(int i, PlayerInventory menu, PlayerEntity player) {
                    return WaystoneSelectionContainer.createWaystoneSelection(i, player, WarpMode.PORTSTONE_TO_WAYSTONE, null);
                }
            }, it -> {
                it.writeByte(WarpMode.PORTSTONE_TO_WAYSTONE.ordinal());
                it.writeBlockPos(pos);
            });
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(HALF);
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

    @Nullable
    @Override
    public PathNodeType getAiPathNodeType(BlockState state, IBlockReader world, BlockPos pos, @Nullable MobEntity entity) {
        return PathNodeType.BLOCKED;
    }

}
