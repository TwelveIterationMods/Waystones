package net.blay09.mods.waystones.worldgen;

import com.mojang.serialization.Codec;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.TopSolidOnce;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class WaystonePlacement extends TopSolidOnce {

    public WaystonePlacement(Codec<NoPlacementConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(WorldDecoratingHelper helper, Random random, NoPlacementConfig config, BlockPos pos) {
        if (isWaystoneChunk(helper, pos)) {
            if (helper.field_242889_a.getWorld().getDimensionKey() == World.THE_NETHER) {
                BlockPos.Mutable mutablePos = pos.toMutable();
                int topMostY = helper.func_242893_a(func_241858_a(config), pos.getX(), pos.getZ());
                mutablePos.setY(topMostY);
                BlockState stateAbove = helper.field_242889_a.getBlockState(mutablePos);
                for (int i = mutablePos.getY(); i >= 1; i--) {
                    mutablePos.setY(mutablePos.getY() - 1);
                    BlockState state = helper.field_242889_a.getBlockState(mutablePos);
                    if(!state.isAir() && state.getFluidState().isEmpty() && stateAbove.isAir() && !state.isIn(Blocks.BEDROCK)) {
                        mutablePos.setY(mutablePos.getY() + 1);
                        break;
                    }
                    stateAbove = state;
                }
                return mutablePos.getY() > 0 ? Stream.of(mutablePos) : Stream.empty();
            }
            return super.getPositions(helper, random, config, pos);
        } else {
            return Stream.empty();
        }
    }

    private boolean isWaystoneChunk(WorldDecoratingHelper world, BlockPos pos) {
        final int chunkDistance = WaystonesConfig.COMMON.worldGenFrequency.get();
        if (chunkDistance == 0) {
            return false;
        }

        ResourceLocation dimension = world.field_242889_a.getWorld().getDimensionKey().getLocation();
        List<? extends String> dimensionAllowList = WaystonesConfig.COMMON.worldGenDimensionAllowList.get();
        List<? extends String> dimensionDenyList = WaystonesConfig.COMMON.worldGenDimensionDenyList.get();
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
        long seed = world.field_242889_a.getSeed();
        Random random = new Random(seed * devGridX * devGridZ);
        int chunkOffsetX = random.nextInt(maxDeviation);
        int chunkOffsetZ = random.nextInt(maxDeviation);
        return (chunkX + chunkOffsetX) % chunkDistance == 0 && (chunkZ + chunkOffsetZ) % chunkDistance == 0;
    }
}
