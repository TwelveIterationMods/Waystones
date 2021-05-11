package net.blay09.mods.waystones.block;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.container.WaystoneSelectionContainer;
import net.blay09.mods.waystones.tileentity.WarpPlateTileEntity;
import net.blay09.mods.waystones.tileentity.WaystoneTileEntityBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Random;

public class WarpPlateBlock extends WaystoneBlockBase {

    private static final VoxelShape SHAPE = VoxelShapes.or(
            makeCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0),
            makeCuboidShape(3.0, 1.0, 3.0, 13.0, 2.0, 13.0)
    ).simplify();

    public WarpPlateBlock() {
        this.setDefaultState(this.stateContainer.getBaseState().with(WATERLOGGED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entityIn) {
        super.onEntityCollision(state, world, pos, entityIn);
    }

    @Override
    public void animateTick(BlockState state, World worldIn, BlockPos pos, Random rand) {
        super.animateTick(state, worldIn, pos, rand);

        for (int i = 0; i < 50; i++) {
            worldIn.addParticle(ParticleTypes.PORTAL, pos.getX() + Math.random(), pos.getY() + Math.random() * 2, pos.getZ() + Math.random(), 0f, 1f, 0f);
            worldIn.addParticle(ParticleTypes.REVERSE_PORTAL, pos.getX() + Math.random(), pos.getY() + Math.random(), pos.getZ() + Math.random(), 0f, 0.1f, 0f);
        }
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new WarpPlateTileEntity();
    }

    @Override
    protected void handleActivation(World world, BlockPos pos, PlayerEntity player, WaystoneTileEntityBase tileEntity, IWaystone waystone) {
        if (!world.isRemote) {
            NetworkHooks.openGui(((ServerPlayerEntity) player), tileEntity.getWaystoneSelectionContainerProvider(), it -> WaystoneSelectionContainer.writeSharestoneContainer(it, pos));
        }
    }
}
