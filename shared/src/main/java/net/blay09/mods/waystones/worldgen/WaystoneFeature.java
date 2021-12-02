package net.blay09.mods.waystones.worldgen;

import com.mojang.serialization.Codec;
import net.blay09.mods.waystones.block.WaystoneBlock;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.Random;

public class WaystoneFeature extends Feature<NoneFeatureConfiguration> {

    private final BlockState waystoneState;

    public WaystoneFeature(Codec<NoneFeatureConfiguration> codec, BlockState waystoneState) {
        super(codec);
        this.waystoneState = waystoneState;
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel world = context.level();
        BlockPos pos = context.origin();
        Random random = context.random();
        Direction facing = Direction.values()[2 + random.nextInt(4)];
        BlockState state = world.getBlockState(pos);
        BlockPos posAbove = pos.above();
        BlockState stateAbove = world.getBlockState(posAbove);
        if (state.isAir() && stateAbove.isAir()) {
            world.setBlock(pos, waystoneState
                    .setValue(WaystoneBlock.HALF, DoubleBlockHalf.LOWER)
                    .setValue(WaystoneBlock.FACING, facing), 2);

            world.setBlock(posAbove, waystoneState
                    .setValue(WaystoneBlock.HALF, DoubleBlockHalf.UPPER)
                    .setValue(WaystoneBlock.FACING, facing), 2);

            WaystoneBlockEntity tileEntity = (WaystoneBlockEntity) world.getBlockEntity(pos);
            if (tileEntity != null) {
                tileEntity.initializeWaystone(world, null, true);

                BlockEntity tileEntityAbove = world.getBlockEntity(pos.above());
                if (tileEntityAbove instanceof WaystoneBlockEntity) {
                    ((WaystoneBlockEntity) tileEntityAbove).initializeFromBase(tileEntity);
                }
            }

            return true;
        }

        return false;
    }

}
