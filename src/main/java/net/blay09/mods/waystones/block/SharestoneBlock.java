package net.blay09.mods.waystones.block;

import net.blay09.mods.waystones.tileentity.SharestoneTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class SharestoneBlock extends WaystoneBlockBase {

    private static final VoxelShape LOWER_SHAPE = VoxelShapes.or(
            makeCuboidShape(0.0, 0.0, 0.0, 16.0, 3.0, 16.0),
            makeCuboidShape(1.0, 3.0, 1.0, 15.0, 7.0, 15.0),
            makeCuboidShape(2.0, 7.0, 2.0, 14.0, 9.0, 14.0),
            makeCuboidShape(3.0, 9.0, 3.0, 13.0, 16.0, 13.0)
    ).simplify();

    private static final VoxelShape UPPER_SHAPE = VoxelShapes.or(
            makeCuboidShape(3.0, 0.0, 3.0, 13.0, 7.0, 13.0),
            makeCuboidShape(2.0, 7.0, 2.0, 14.0, 9.0, 14.0),
            makeCuboidShape(1.0, 9.0, 1.0, 15.0, 13.0, 15.0),
            makeCuboidShape(0.0, 13.0, 0.0, 16.0, 16.0, 16.0)
    ).simplify();

    public SharestoneBlock() {
        this.setDefaultState(this.stateContainer.getBaseState().with(HALF, DoubleBlockHalf.LOWER).with(WATERLOGGED, false));
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new SharestoneTileEntity();
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return state.get(HALF) == DoubleBlockHalf.UPPER ? UPPER_SHAPE : LOWER_SHAPE;
    }
}
