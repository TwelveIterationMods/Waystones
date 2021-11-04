package net.blay09.mods.waystones.worldgen;

import com.mojang.datafixers.util.Pair;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.config.WorldGenStyle;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.LegacySingleJigsawPiece;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Mod.EventBusSubscriber(modid = Waystones.MOD_ID)
public class ModWorldGen {
    private static final ResourceLocation villageWaystoneStructure = new ResourceLocation("waystones", "village/common/waystone");
    private static final ResourceLocation desertVillageWaystoneStructure = new ResourceLocation("waystones", "village/desert/waystone");
    private static final ResourceLocation emptyStructure = new ResourceLocation("empty");

    private static WaystoneFeature waystoneFeature;
    private static WaystoneFeature mossyWaystoneFeature;
    private static WaystoneFeature sandyWaystoneFeature;
    private static ConfiguredFeature<?, ?> configuredWaystoneFeature;
    private static ConfiguredFeature<?, ?> configuredMossyWaystoneFeature;
    private static ConfiguredFeature<?, ?> configuredSandyWaystoneFeature;
    private static WaystonePlacement waystonePlacement;

    public static void registerFeatures(IForgeRegistry<Feature<?>> registry) {
        registry.registerAll(
                waystoneFeature = (WaystoneFeature) new WaystoneFeature(NoFeatureConfig.field_236558_a_, ModBlocks.waystone.getDefaultState()).setRegistryName("waystone"),
                mossyWaystoneFeature = (WaystoneFeature) new WaystoneFeature(NoFeatureConfig.field_236558_a_, ModBlocks.mossyWaystone.getDefaultState()).setRegistryName("mossy_waystone"),
                sandyWaystoneFeature = (WaystoneFeature) new WaystoneFeature(NoFeatureConfig.field_236558_a_, ModBlocks.sandyWaystone.getDefaultState()).setRegistryName("sandy_waystone")
        );
    }

    public static void registerConfiguredFeatures() {
        configuredWaystoneFeature = registerConfiguredWaystone(waystoneFeature);
        configuredMossyWaystoneFeature = registerConfiguredWaystone(mossyWaystoneFeature);
        configuredSandyWaystoneFeature = registerConfiguredWaystone(sandyWaystoneFeature);
    }

    private static ConfiguredFeature<?, ?> registerConfiguredWaystone(WaystoneFeature feature) {
        ConfiguredFeature<?, ?> configuredFeature = feature
                .withConfiguration(NoFeatureConfig.NO_FEATURE_CONFIG)
                .withPlacement(waystonePlacement.configure(NoPlacementConfig.NO_PLACEMENT_CONFIG));
        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, Objects.requireNonNull(feature.getRegistryName()), configuredFeature);
        return configuredFeature;
    }

    public static void registerPlacements(IForgeRegistry<Placement<?>> registry) {
        registry.registerAll(
                waystonePlacement = (WaystonePlacement) new WaystonePlacement(NoPlacementConfig.CODEC).setRegistryName("waystone")
        );
    }

    @SubscribeEvent
    public static void onBiomeLoading(BiomeLoadingEvent event) {
        ConfiguredFeature<?, ?>  configuredFeature = getWaystoneFeature(event.getCategory());
        event.getGeneration().withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, configuredFeature);
    }

    private static ConfiguredFeature<?, ?> getWaystoneFeature(Biome.Category biomeCategory) {
        WorldGenStyle worldGenStyle = WaystonesConfig.COMMON.worldGenStyle.get();
        switch (worldGenStyle) {
            case MOSSY:
                return configuredMossyWaystoneFeature;
            case SANDY:
                return configuredSandyWaystoneFeature;
            case BIOME:
                switch (biomeCategory) {
                    case DESERT:
                        return configuredSandyWaystoneFeature;
                    case JUNGLE:
                    case SWAMP:
                    case MUSHROOM:
                        return configuredMossyWaystoneFeature;
                    default:
                        return configuredWaystoneFeature;
                }
            default:
                return configuredWaystoneFeature;
        }
    }

    public static void setupVillageWorldGen(DynamicRegistries dynamicRegistries) {
        if (WaystonesConfig.COMMON.spawnInVillages.get() || WaystonesConfig.COMMON.forceSpawnInVillages.get()) {

            // Add Waystone to Vanilla Villages.
            addWaystoneStructureToVillageConfig(dynamicRegistries, "village/plains/houses", villageWaystoneStructure, 1);
            addWaystoneStructureToVillageConfig(dynamicRegistries, "village/snowy/houses", villageWaystoneStructure, 1);
            addWaystoneStructureToVillageConfig(dynamicRegistries, "village/savanna/houses", villageWaystoneStructure, 1);
            addWaystoneStructureToVillageConfig(dynamicRegistries, "village/desert/houses", desertVillageWaystoneStructure, 1);
            addWaystoneStructureToVillageConfig(dynamicRegistries, "village/taiga/houses", villageWaystoneStructure, 1);

            // Add Waystone to other mod's structures. (Make sure Waystone piece Jigsaw Block's Name matches the other mod piece Jigsaw's Target Name.
            addWaystoneStructureToVillageConfig(dynamicRegistries, "repurposed_structures:village/badlands/houses", desertVillageWaystoneStructure, 1);
            addWaystoneStructureToVillageConfig(dynamicRegistries, "repurposed_structures:village/birch/houses", villageWaystoneStructure, 1);
            addWaystoneStructureToVillageConfig(dynamicRegistries, "repurposed_structures:village/dark_forest/houses", villageWaystoneStructure, 1);
            addWaystoneStructureToVillageConfig(dynamicRegistries, "repurposed_structures:village/jungle/houses", villageWaystoneStructure, 1);
            addWaystoneStructureToVillageConfig(dynamicRegistries, "repurposed_structures:village/mountains/houses", villageWaystoneStructure, 1);
            addWaystoneStructureToVillageConfig(dynamicRegistries, "repurposed_structures:village/oak/houses", villageWaystoneStructure, 1);
            addWaystoneStructureToVillageConfig(dynamicRegistries, "repurposed_structures:village/swamp/houses", villageWaystoneStructure, 1);
        }
    }

    private static void addWaystoneStructureToVillageConfig(DynamicRegistries dynamicRegistries, String villagePiece, ResourceLocation waystoneStructure, int weight) {
        LegacySingleJigsawPiece piece = JigsawPiece.func_242849_a(waystoneStructure.toString()).apply(JigsawPattern.PlacementBehaviour.RIGID);
        JigsawPattern pool = dynamicRegistries.getRegistry(Registry.JIGSAW_POOL_KEY).getOptional(new ResourceLocation(villagePiece)).orElse(null);
        if(pool != null) {
            // pretty sure this can be an immutable list (when datapacked) so gotta make a copy to be safe.
            List<JigsawPiece> listOfPieces = new ArrayList<>(pool.jigsawPieces);
            for(int i = 0; i < weight; i++){
                listOfPieces.add(piece);
            }
            pool.jigsawPieces = listOfPieces;

            List<Pair<JigsawPiece, Integer>> listOfWeightedPieces = new ArrayList<>(pool.rawTemplates);
            listOfWeightedPieces.add(new Pair(piece, weight));
            pool.rawTemplates = listOfWeightedPieces;
        }
    }
}
