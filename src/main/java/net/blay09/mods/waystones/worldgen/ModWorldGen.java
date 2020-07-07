//package net.blay09.mods.waystones.worldgen;
//
//import com.google.common.collect.ImmutableList;
//import com.mojang.datafixers.util.Pair;
//import net.blay09.mods.waystones.block.ModBlocks;
//import net.blay09.mods.waystones.config.WaystoneConfig;
//import net.blay09.mods.waystones.config.WorldGenStyle;
//import net.minecraft.util.ResourceLocation;
//import net.minecraft.world.biome.Biome;
//import net.minecraft.world.gen.GenerationStage;
//import net.minecraft.world.gen.feature.ConfiguredFeature;
//import net.minecraft.world.gen.feature.Feature;
//import net.minecraft.world.gen.feature.NoFeatureConfig;
//import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
//import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
//import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
//import net.minecraft.world.gen.feature.jigsaw.SingleJigsawPiece;
//import net.minecraft.world.gen.feature.structure.*;
//import net.minecraft.world.gen.placement.NoPlacementConfig;
//import net.minecraft.world.gen.placement.Placement;
//import net.minecraftforge.common.BiomeDictionary;
//import net.minecraftforge.registries.ForgeRegistries;
//import net.minecraftforge.registries.IForgeRegistry;
//
//import java.util.Collections;
//
//import static net.minecraftforge.common.BiomeDictionary.Type;
//
//public class ModWorldGen {
//    private static final ResourceLocation villageWaystoneStructure = new ResourceLocation("waystones", "village/common/waystone");
//    private static final ResourceLocation desertVillageWaystoneStructure = new ResourceLocation("waystones", "village/desert/waystone");
//    private static final ResourceLocation emptyStructure = new ResourceLocation("empty");
//
//    private static WaystoneFeature waystoneFeature;
//    private static WaystoneFeature mossyWaystoneFeature;
//    private static WaystoneFeature sandyWaystoneFeature;
//    private static WaystonePlacement waystonePlacement;
//
//    public static void registerFeatures(IForgeRegistry<Feature<?>> registry) {
//        registry.registerAll(
//                waystoneFeature = (WaystoneFeature) new WaystoneFeature(NoFeatureConfig::deserialize, ModBlocks.waystone.getDefaultState()).setRegistryName("waystone"),
//                mossyWaystoneFeature = (WaystoneFeature) new WaystoneFeature(NoFeatureConfig::deserialize, ModBlocks.mossyWaystone.getDefaultState()).setRegistryName("mossy_waystone"),
//                sandyWaystoneFeature = (WaystoneFeature) new WaystoneFeature(NoFeatureConfig::deserialize, ModBlocks.sandyWaystone.getDefaultState()).setRegistryName("sandy_waystone")
//        );
//    }
//
//    public static void registerPlacements(IForgeRegistry<Placement<?>> registry) {
//        registry.registerAll(
//                waystonePlacement = (WaystonePlacement) new WaystonePlacement(NoPlacementConfig::deserialize).setRegistryName("waystone")
//        );
//    }
//
//    public static void setupRandomWorldGen() {
//        ForgeRegistries.BIOMES.forEach(biome -> {
//            WaystoneFeature feature = getWaystoneFeature(biome);
//            ConfiguredFeature<?, ?> configuredFeature = feature
//                    .withConfiguration(NoFeatureConfig.NO_FEATURE_CONFIG)
//                    .func_227228_a_(waystonePlacement.func_227446_a_(NoPlacementConfig.NO_PLACEMENT_CONFIG));
//            biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, configuredFeature);
//        });
//    }
//
//    private static WaystoneFeature getWaystoneFeature(Biome biome) {
//        WorldGenStyle worldGenStyle = WaystoneConfig.COMMON.worldGenStyle.get();
//        switch (worldGenStyle) {
//            case MOSSY:
//                return mossyWaystoneFeature;
//            case SANDY:
//                return sandyWaystoneFeature;
//            case BIOME:
//                if (BiomeDictionary.hasType(biome, Type.SANDY)) {
//                    return sandyWaystoneFeature;
//                } else if (BiomeDictionary.hasType(biome, Type.WET)) {
//                    return mossyWaystoneFeature;
//                } else {
//                    return waystoneFeature;
//                }
//            default:
//                return waystoneFeature;
//        }
//    }
//
//    public static void setupVillageWorldGen() {
//        JigsawManager.REGISTRY.register(new JigsawPattern(villageWaystoneStructure, emptyStructure, Collections.emptyList(), JigsawPattern.PlacementBehaviour.RIGID));
//        JigsawManager.REGISTRY.register(new JigsawPattern(desertVillageWaystoneStructure, emptyStructure, Collections.emptyList(), JigsawPattern.PlacementBehaviour.RIGID));
//
//        if (WaystoneConfig.COMMON.addVillageStructure.get()) {
//            PlainsVillagePools.init();
//            SnowyVillagePools.init();
//            SavannaVillagePools.init();
//            DesertVillagePools.init();
//            TaigaVillagePools.init();
//
//            addWaystoneStructureToVillageConfig("village/plains/houses", villageWaystoneStructure);
//            addWaystoneStructureToVillageConfig("village/snowy/houses", villageWaystoneStructure);
//            addWaystoneStructureToVillageConfig("village/savanna/houses", villageWaystoneStructure);
//            addWaystoneStructureToVillageConfig("village/desert/houses", desertVillageWaystoneStructure);
//            addWaystoneStructureToVillageConfig("village/taiga/houses", villageWaystoneStructure);
//        }
//    }
//
//    private static void addWaystoneStructureToVillageConfig(String villagePiece, ResourceLocation waystoneStructure) {
//        JigsawPattern houses = JigsawManager.REGISTRY.get(new ResourceLocation(villagePiece));
//
//        final SingleJigsawPiece piece = new SingleJigsawPiece(waystoneStructure.toString(), Collections.emptyList(), JigsawPattern.PlacementBehaviour.RIGID);
//        houses.field_214952_d = ImmutableList.<Pair<JigsawPiece, Integer>>builder().addAll(houses.field_214952_d).add(Pair.of(piece, 1)).build();
//        houses.jigsawPieces.add(piece);
//    }
//}
