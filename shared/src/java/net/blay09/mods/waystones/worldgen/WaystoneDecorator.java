package net.blay09.mods.waystones.worldgen;

import com.mojang.serialization.Codec;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.HeightmapConfiguration;
import net.minecraft.world.level.levelgen.placement.DecorationContext;
import net.minecraft.world.level.levelgen.placement.HeightmapDecorator;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class WaystoneDecorator extends HeightmapDecorator {

    public WaystoneDecorator(Codec<HeightmapConfiguration> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(DecorationContext context, Random random, HeightmapConfiguration config, BlockPos pos) {
        if (isWaystoneChunk(context, pos)) {
            if (context.getLevel().getLevel().dimension() == Level.NETHER) {
                BlockPos.MutableBlockPos mutablePos = pos.mutable();
                int topMostY = context.getHeight(config.heightmap, pos.getX(), pos.getZ());
                mutablePos.setY(topMostY);
                BlockState stateAbove = context.getLevel().getBlockState(mutablePos);
                for (int i = mutablePos.getY(); i >= 1; i--) {
                    mutablePos.setY(mutablePos.getY() - 1);
                    BlockState state = context.getLevel().getBlockState(mutablePos);
                    if (!state.isAir() && state.getFluidState().isEmpty() && stateAbove.isAir() && !state.is(Blocks.BEDROCK)) {
                        mutablePos.setY(mutablePos.getY() + 1);
                        break;
                    }
                    stateAbove = state;
                }
                return mutablePos.getY() > 0 ? Stream.of(mutablePos) : Stream.empty();
            }
            return super.getPositions(context, random, config, pos);
        } else {
            return Stream.empty();
        }
    }

    private boolean isWaystoneChunk(DecorationContext world, BlockPos pos) {
        final int chunkDistance = WaystonesConfig.getActive().worldGenFrequency();
        if (chunkDistance == 0) {
            return false;
        }

        ResourceLocation dimension = world.getLevel().getLevel().dimension().location();
        List<? extends String> dimensionAllowList = WaystonesConfig.getActive().worldGenDimensionAllowList();
        List<? extends String> dimensionDenyList = WaystonesConfig.getActive().worldGenDimensionDenyList();
        if (!dimensionAllowList.isEmpty() && !dimensionAllowList.contains(dimension.toString())) {
            return false;
        } else if (!dimensionDenyList.isEmpty() && dimensionDenyList.contains(dimension.toString())) {
            return false;
        }

        final int maxDeviation = (int) Math.ceil(chunkDistance / 2f);
        int chunkX = pos.getX() / 16;
        int chunkZ = pos.getZ() / 16;
        int devGridX = pos.getX() / 16 * maxDeviation;
        int devGridZ = pos.getZ() / 16 * maxDeviation;
        long seed = world.getLevel().getSeed();
        Random random = new Random(seed * devGridX * devGridZ);
        int chunkOffsetX = random.nextInt(maxDeviation);
        int chunkOffsetZ = random.nextInt(maxDeviation);
        return (chunkX + chunkOffsetX) % chunkDistance == 0 && (chunkZ + chunkOffsetZ) % chunkDistance == 0;
    }
}
