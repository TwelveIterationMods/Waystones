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
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.structures.LegacySinglePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
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
    private static DeferredObject<PlacedFeature> placedWaystoneFeature;
    private static DeferredObject<PlacedFeature> placedMossyWaystoneFeature;
    private static DeferredObject<PlacedFeature> placedSandyWaystoneFeature;
    public static DeferredObject<PlacementModifierType<WaystonePlacement>> waystonePlacement;

    public static void initialize(BalmWorldGen worldGen) {
        waystoneFeature = worldGen.registerFeature(() -> new WaystoneFeature(NoneFeatureConfiguration.CODEC, ModBlocks.waystone.defaultBlockState()), id("waystone"));
        mossyWaystoneFeature = worldGen.registerFeature(() -> new WaystoneFeature(NoneFeatureConfiguration.CODEC, ModBlocks.mossyWaystone.defaultBlockState()), id("mossy_waystone"));
        sandyWaystoneFeature = worldGen.registerFeature(() -> new WaystoneFeature(NoneFeatureConfiguration.CODEC, ModBlocks.sandyWaystone.defaultBlockState()), id("sandy_waystone"));

        waystonePlacement = worldGen.registerPlacementModifier(() -> () -> WaystonePlacement.CODEC, id("waystone"));

        configuredWaystoneFeature = worldGen.registerConfiguredFeature(() -> worldGen.configuredFeature(waystoneFeature.get(), FeatureConfiguration.NONE), id("waystone"));
        configuredMossyWaystoneFeature = worldGen.registerConfiguredFeature(() -> worldGen.configuredFeature(mossyWaystoneFeature.get(), FeatureConfiguration.NONE), id("mossy_waystone"));
        configuredSandyWaystoneFeature = worldGen.registerConfiguredFeature(() -> worldGen.configuredFeature(sandyWaystoneFeature.get(), FeatureConfiguration.NONE), id("sandy_waystone"));

        placedWaystoneFeature = worldGen.registerPlacedFeature(() -> worldGen.placedFeature(configuredWaystoneFeature.get(), new WaystonePlacement()), id("waystone"));
        placedMossyWaystoneFeature = worldGen.registerPlacedFeature(() -> worldGen.placedFeature(configuredMossyWaystoneFeature.get(), new WaystonePlacement()), id("mossy_waystone"));
        placedSandyWaystoneFeature = worldGen.registerPlacedFeature(() -> worldGen.placedFeature(configuredSandyWaystoneFeature.get(), new WaystonePlacement()), id("sandy_waystone"));

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
        }

        // Registers a condition for repurposed structures compat
        Registry.REGISTRY.getOptional(new ResourceLocation("repurposed_structures", "json_conditions"))
            .ifPresent(registry -> Registry.register(
                (Registry<Supplier<Boolean>>)registry,
                new ResourceLocation("waystones", "config"),
                () -> WaystonesConfig.getActive().spawnInVillages() || WaystonesConfig.getActive().forceSpawnInVillages()));
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
