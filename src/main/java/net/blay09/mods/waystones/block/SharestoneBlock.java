package net.blay09.mods.waystones.block;

import net.blay09.mods.waystones.tileentity.SharestoneTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class SharestoneBlock extends WaystoneBlock {

    public SharestoneBlock() {
        this.setDefaultState(this.stateContainer.getBaseState().with(HALF, DoubleBlockHalf.LOWER).with(WATERLOGGED, false));
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new SharestoneTileEntity();
    }
}
