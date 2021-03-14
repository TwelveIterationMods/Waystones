package net.blay09.mods.waystones.worldgen;

import com.mojang.serialization.Codec;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.minecraft.util.RegistryKey;
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
    public Stream<BlockPos> getPositions(WorldDecoratingHelper world, Random random, NoPlacementConfig config, BlockPos pos) {
        if (isWaystoneChunk(world, pos)) {
            return super.getPositions(world, random, config, pos);
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
