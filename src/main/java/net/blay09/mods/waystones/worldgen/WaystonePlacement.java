//package net.blay09.mods.waystones.worldgen;
//
//import com.mojang.serialization.Dynamic;
//import net.blay09.mods.waystones.config.WaystoneConfig;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.IWorld;
//import net.minecraft.world.gen.placement.NoPlacementConfig;
//import net.minecraft.world.gen.placement.TopSolidOnce;
//
//import java.util.Random;
//import java.util.function.Function;
//import java.util.stream.Stream;
//
//public class WaystonePlacement extends TopSolidOnce {
//    public WaystonePlacement(Function<Dynamic<?>, ? extends NoPlacementConfig> configFactory) {
//        super(configFactory);
//    }
//
//    @Override
//    public Stream<BlockPos> getPositions(IWorld world, ChunkGenerator<? extends GenerationSettings> chunkGenerator, Random random, NoPlacementConfig config, BlockPos pos) {
//        if (isWaystoneChunk(world, pos)) {
//            return super.getPositions(world, chunkGenerator, random, config, pos);
//        } else {
//            return Stream.empty();
//        }
//    }
//
//    private boolean isWaystoneChunk(IWorld world, BlockPos pos) {
//        final int chunkDistance = WaystoneConfig.COMMON.worldGenFrequency.get();
//        if (chunkDistance == 0) {
//            return false;
//        }
//
//        final int maxDeviation = (int) Math.ceil(chunkDistance / 2f);
//        int chunkX = pos.getX() / 16;
//        int chunkZ = pos.getZ() / 16;
//        int devGridX = pos.getX() / 16 * maxDeviation;
//        int devGridZ = pos.getZ() / 16 * maxDeviation;
//        Random random = new Random(world.getSeed() * devGridX * devGridZ);
//        int chunkOffsetX = random.nextInt(maxDeviation);
//        int chunkOffsetZ = random.nextInt(maxDeviation);
//        return (chunkX + chunkOffsetX) % chunkDistance == 0 && (chunkZ + chunkOffsetZ) % chunkDistance == 0;
//    }
//}
