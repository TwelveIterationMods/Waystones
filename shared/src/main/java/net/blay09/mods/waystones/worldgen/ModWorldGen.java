package net.blay09.mods.waystones.worldgen;

import com.mojang.datafixers.util.Pair;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.event.server.ServerReloadedEvent;
import net.blay09.mods.balm.api.event.server.ServerStartedEvent;
import net.blay09.mods.balm.api.world.BalmWorldGen;
import net.blay09.mods.balm.api.world.BiomePredicate;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.config.WorldGenStyle;
import net.blay09.mods.waystones.mixin.StructureTemplatePoolAccessor;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.structures.LegacySinglePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.ArrayList;
import java.util.List;

public class ModWorldGen {
    private static final ResourceLocation villageWaystoneStructure = new ResourceLocation("waystones", "village/common/waystone");
    private static final ResourceLocation desertVillageWaystoneStructure = new ResourceLocation("waystones", "village/desert/waystone");
    private static final ResourceLocation emptyStructure = new ResourceLocation("empty");

    private static DeferredObject<WaystoneFeature> waystoneFeature;
    private static DeferredObject<WaystoneFeature> mossyWaystoneFeature;
    private static DeferredObject<WaystoneFeature> sandyWaystoneFeature;
    private static DeferredObject<ConfiguredFeature<?, ?>> configuredWaystoneFeature;
    private static DeferredObject<ConfiguredFeature<?, ?>> configuredMossyWaystoneFeature;
    private static DeferredObject<ConfiguredFeature<?, ?>> configuredSandyWaystoneFeature;
    public static DeferredObject<PlacementModifierType<WaystonePlacement>> waystonePlacement;

    public static void initialize(BalmWorldGen worldGen) {
        waystoneFeature = worldGen.registerFeature(() -> new WaystoneFeature(NoneFeatureConfiguration.CODEC, ModBlocks.waystone.defaultBlockState()), id("waystone"));
        mossyWaystoneFeature = worldGen.registerFeature(() -> new WaystoneFeature(NoneFeatureConfiguration.CODEC, ModBlocks.mossyWaystone.defaultBlockState()), id("mossy_waystone"));
        sandyWaystoneFeature = worldGen.registerFeature(() -> new WaystoneFeature(NoneFeatureConfiguration.CODEC, ModBlocks.sandyWaystone.defaultBlockState()), id("sandy_waystone"));

        waystonePlacement = worldGen.registerPlacementModifier(() -> () -> WaystonePlacement.CODEC, id("waystone"));

//        Supplier<ConfiguredDecorator<HeightmapConfiguration>> configuredDecorator = () -> waystonePlacement.get().configured(new HeightmapConfiguration(Heightmap.Types.OCEAN_FLOOR_WG)); TODO 1.18
//        configuredWaystoneFeature = worldGen.registerConfiguredFeature(() -> worldGen.configuredFeature(waystoneFeature.get(), FeatureConfiguration.NONE, configuredDecorator.get()), id("waystone"));
//        configuredMossyWaystoneFeature = worldGen.registerConfiguredFeature(() -> worldGen.configuredFeature(mossyWaystoneFeature.get(), FeatureConfiguration.NONE, configuredDecorator.get()), id("mossy_waystone"));
//        configuredSandyWaystoneFeature = worldGen.registerConfiguredFeature(() -> worldGen.configuredFeature(sandyWaystoneFeature.get(), FeatureConfiguration.NONE, configuredDecorator.get()), id("sandy_waystone"));

        worldGen.addFeatureToBiomes(matchesCategory(Biome.BiomeCategory.DESERT), GenerationStep.Decoration.VEGETAL_DECORATION, getWaystoneFeature(WorldGenStyle.SANDY));
        worldGen.addFeatureToBiomes(matchesCategory(Biome.BiomeCategory.JUNGLE), GenerationStep.Decoration.VEGETAL_DECORATION, getWaystoneFeature(WorldGenStyle.MOSSY));
        worldGen.addFeatureToBiomes(matchesCategory(Biome.BiomeCategory.SWAMP), GenerationStep.Decoration.VEGETAL_DECORATION, getWaystoneFeature(WorldGenStyle.MOSSY));
        worldGen.addFeatureToBiomes(matchesCategory(Biome.BiomeCategory.MUSHROOM), GenerationStep.Decoration.VEGETAL_DECORATION, getWaystoneFeature(WorldGenStyle.MOSSY));
        worldGen.addFeatureToBiomes(matchesNeitherCategory(Biome.BiomeCategory.SWAMP, Biome.BiomeCategory.DESERT, Biome.BiomeCategory.JUNGLE, Biome.BiomeCategory.MUSHROOM), GenerationStep.Decoration.VEGETAL_DECORATION, getWaystoneFeature(WorldGenStyle.DEFAULT));

        Balm.getEvents().onEvent(ServerStartedEvent.class, event -> setupVillageWorldGen(event.getServer().registryAccess()));
        Balm.getEvents().onEvent(ServerReloadedEvent.class, event -> setupVillageWorldGen(event.getServer().registryAccess()));
    }

    private static BiomePredicate matchesCategory(Biome.BiomeCategory category) {
        return (resourceLocation, biomeCategory, precipitation, v, v1) -> category == biomeCategory;
    }

    private static BiomePredicate matchesNeitherCategory(Biome.BiomeCategory... categories) {
        return (resourceLocation, biomeCategory, precipitation, v, v1) -> {
            for (Biome.BiomeCategory category : categories) {
                if (category == biomeCategory) {
                    return false;
                }
            }

            return true;
        };
    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(Waystones.MOD_ID, name);
    }

    private static ResourceLocation getWaystoneFeature(WorldGenStyle biomeWorldGenStyle) {
        WorldGenStyle worldGenStyle = WaystonesConfig.getActive().worldGenStyle();
        return switch (worldGenStyle) {
            case MOSSY -> configuredMossyWaystoneFeature.getIdentifier();
            case SANDY -> configuredSandyWaystoneFeature.getIdentifier();
            case BIOME -> switch (biomeWorldGenStyle) {
                case SANDY -> configuredSandyWaystoneFeature.getIdentifier();
                case MOSSY -> configuredMossyWaystoneFeature.getIdentifier();
                default -> configuredWaystoneFeature.getIdentifier();
            };
            default -> configuredWaystoneFeature.getIdentifier();
        };
    }

    public static void setupVillageWorldGen(RegistryAccess registryAccess) {
        if (WaystonesConfig.getActive().spawnInVillages() || WaystonesConfig.getActive().forceSpawnInVillages()) {
            // Add Waystone to Vanilla Villages.
            addWaystoneStructureToVillageConfig(registryAccess, "village/plains/houses", villageWaystoneStructure, 1);
            addWaystoneStructureToVillageConfig(registryAccess, "village/snowy/houses", villageWaystoneStructure, 1);
            addWaystoneStructureToVillageConfig(registryAccess, "village/savanna/houses", villageWaystoneStructure, 1);
            addWaystoneStructureToVillageConfig(registryAccess, "village/desert/houses", desertVillageWaystoneStructure, 1);
            addWaystoneStructureToVillageConfig(registryAccess, "village/taiga/houses", villageWaystoneStructure, 1);

            // Add Waystone to other mod's structures. (Make sure Waystone piece Jigsaw Block's Name matches the other mod piece Jigsaw's Target Name.
            addWaystoneStructureToVillageConfig(registryAccess, "repurposed_structures:villages/badlands/houses", desertVillageWaystoneStructure, 1);
            addWaystoneStructureToVillageConfig(registryAccess, "repurposed_structures:villages/birch/houses", villageWaystoneStructure, 1);
            addWaystoneStructureToVillageConfig(registryAccess, "repurposed_structures:villages/dark_forest/houses", villageWaystoneStructure, 1);
            addWaystoneStructureToVillageConfig(registryAccess, "repurposed_structures:villages/giant_taiga/houses", villageWaystoneStructure, 1);
            addWaystoneStructureToVillageConfig(registryAccess, "repurposed_structures:villages/jungle/houses", villageWaystoneStructure, 1);
            addWaystoneStructureToVillageConfig(registryAccess, "repurposed_structures:villages/mountains/houses", villageWaystoneStructure, 1);
            addWaystoneStructureToVillageConfig(registryAccess, "repurposed_structures:villages/mushroom/houses", villageWaystoneStructure, 1);
            addWaystoneStructureToVillageConfig(registryAccess, "repurposed_structures:villages/oak/houses", villageWaystoneStructure, 1);
            addWaystoneStructureToVillageConfig(registryAccess, "repurposed_structures:villages/swamp/houses", villageWaystoneStructure, 1);
        }
    }

    private static void addWaystoneStructureToVillageConfig(RegistryAccess registryAccess, String villagePiece, ResourceLocation waystoneStructure, int weight) {
        LegacySinglePoolElement piece = StructurePoolElement.legacy(waystoneStructure.toString()).apply(StructureTemplatePool.Projection.RIGID);
        StructureTemplatePool pool = registryAccess.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY).getOptional(new ResourceLocation(villagePiece)).orElse(null);
        if (pool != null) {
            var poolAccessor = (StructureTemplatePoolAccessor) pool;
            // pretty sure this can be an immutable list (when datapacked) so gotta make a copy to be safe.
            List<StructurePoolElement> listOfPieces = new ArrayList<>(poolAccessor.getTemplates());
            for (int i = 0; i < weight; i++) {
                listOfPieces.add(piece);
            }
            poolAccessor.setTemplates(listOfPieces);

            List<Pair<StructurePoolElement, Integer>> listOfWeightedPieces = new ArrayList<>(poolAccessor.getRawTemplates());
            listOfWeightedPieces.add(new Pair<>(piece, weight));
            poolAccessor.setRawTemplates(listOfWeightedPieces);
        }
    }
}
