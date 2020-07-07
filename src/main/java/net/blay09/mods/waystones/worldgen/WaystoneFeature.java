package net.blay09.mods.waystones.worldgen;

import com.mojang.serialization.Codec;
import net.blay09.mods.waystones.block.WaystoneBlock;
import net.blay09.mods.waystones.tileentity.WaystoneTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.StructureManager;

import java.util.Random;

public class WaystoneFeature extends Feature<NoFeatureConfig> {

    private final BlockState waystoneState;

    public WaystoneFeature(Codec<NoFeatureConfig> codec, BlockState waystoneState) {
        super(codec);
        this.waystoneState = waystoneState;
    }

    @Override // place
    public boolean func_230362_a_(ISeedReader world, StructureManager structureManager, ChunkGenerator chunkGenerator, Random rand, BlockPos pos, NoFeatureConfig config) {
        Direction facing = Direction.values()[2 + rand.nextInt(4)];
        BlockState state = world.getBlockState(pos);
        BlockPos posAbove = pos.up();
        BlockState stateAbove = world.getBlockState(posAbove);
        if (state.isAir(world, pos) && stateAbove.isAir(world, posAbove)) {
            world.setBlockState(pos, waystoneState
                    .with(WaystoneBlock.HALF, DoubleBlockHalf.LOWER)
                    .with(WaystoneBlock.FACING, facing), 2);

            world.setBlockState(posAbove, waystoneState
                    .with(WaystoneBlock.HALF, DoubleBlockHalf.UPPER)
                    .with(WaystoneBlock.FACING, facing), 2);

            WaystoneTileEntity tileEntity = (WaystoneTileEntity) world.getTileEntity(pos);
            if (tileEntity != null) {
                tileEntity.initializeWaystone(world, null, true);

                TileEntity tileEntityAbove = world.getTileEntity(pos.up());
                if (tileEntityAbove instanceof WaystoneTileEntity) {
                    ((WaystoneTileEntity) tileEntityAbove).initializeFromBase(tileEntity);
                }
            }

            return true;
        }

        return false;
    }

}
