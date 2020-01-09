package net.blay09.mods.waystones.worldgen;

import com.mojang.datafixers.Dynamic;
import net.blay09.mods.waystones.block.WaystoneBlock;
import net.blay09.mods.waystones.tileentity.WaystoneTileEntity;
import net.blay09.mods.waystones.worldgen.namegen.NameGenerator;
import net.minecraft.block.BlockState;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.Random;
import java.util.function.Function;

public class WaystoneFeature extends Feature<NoFeatureConfig> {

    private final BlockState waystoneState;

    public WaystoneFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> configFactory, BlockState waystoneState) {
        super(configFactory);
        this.waystoneState = waystoneState;
    }

    @Override
    public boolean place(IWorld world, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
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

            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof WaystoneTileEntity) {
                WaystoneTileEntity waystoneTileEntity = (WaystoneTileEntity) tileEntity;
                waystoneTileEntity.initializeWaystone(world, null, true);
            }

            return true;
        }

        return false;
    }

}
