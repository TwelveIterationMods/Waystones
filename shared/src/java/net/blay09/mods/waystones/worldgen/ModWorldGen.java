package net.blay09.mods.waystones.worldgen;

import net.blay09.mods.balm.core.DeferredObject;
import net.blay09.mods.balm.worldgen.BalmWorldGen;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.config.WorldGenStyle;
import net.blay09.mods.waystones.mixin.StructureTemplatePoolAccessor;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.HeightmapConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.structures.LegacySinglePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.world.level.levelgen.placement.ConfiguredDecorator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModWorldGen extends BalmWorldGen {
    private static final ResourceLocation villageWaystoneStructure = new ResourceLocation("waystones", "village/common/waystone");
    private static final ResourceLocation desertVillageWaystoneStructure = new ResourceLocation("waystones", "village/desert/waystone");
    private static final ResourceLocation emptyStructure = new ResourceLocation("empty");

    private static DeferredObject<WaystoneFeature> waystoneFeature;
    private static DeferredObject<WaystoneFeature> mossyWaystoneFeature;
    private static DeferredObject<WaystoneFeature> sandyWaystoneFeature;
    private static DeferredObject<ConfiguredFeature<?, ?>> configuredWaystoneFeature;
    private static DeferredObject<ConfiguredFeature<?, ?>> configuredMossyWaystoneFeature;
    private static DeferredObject<ConfiguredFeature<?, ?>> configuredSandyWaystoneFeature;
    private static DeferredObject<WaystoneDecorator> waystoneDecorator;

    public static void initialize() {
        waystoneFeature = registerFeature(() -> new WaystoneFeature(NoneFeatureConfiguration.CODEC, ModBlocks.waystone.defaultBlockState()), id("waystone"));
        mossyWaystoneFeature = registerFeature(() -> new WaystoneFeature(NoneFeatureConfiguration.CODEC, ModBlocks.mossyWaystone.defaultBlockState()), id("mossy_waystone"));
        sandyWaystoneFeature = registerFeature(() -> new WaystoneFeature(NoneFeatureConfiguration.CODEC, ModBlocks.sandyWaystone.defaultBlockState()), id("sandy_waystone"));

        waystoneDecorator = registerDecorator(() -> new WaystoneDecorator(HeightmapConfiguration.CODEC), id("waystone"));

        Supplier<ConfiguredDecorator<HeightmapConfiguration>> configuredDecorator = () -> waystoneDecorator.get().configured(new HeightmapConfiguration(Heightmap.Types.OCEAN_FLOOR_WG));
        configuredWaystoneFeature = registerConfiguredFeature(() -> configuredFeature(waystoneFeature.get(), FeatureConfiguration.NONE, configuredDecorator.get()), id("waystone"));
        configuredMossyWaystoneFeature = registerConfiguredFeature(() -> configuredFeature(mossyWaystoneFeature.get(), FeatureConfiguration.NONE, configuredDecorator.get()), id("mossy_waystone"));
        configuredSandyWaystoneFeature = registerConfiguredFeature(() -> configuredFeature(sandyWaystoneFeature.get(), FeatureConfiguration.NONE, configuredDecorator.get()), id("sandy_waystone"));

        addFeatureToBiomes(it -> it.getBiomeCategory() == Biome.BiomeCategory.DESERT, GenerationStep.Decoration.VEGETAL_DECORATION, getWaystoneFeature(WorldGenStyle.SANDY));
        addFeatureToBiomes(it -> it.getBiomeCategory() == Biome.BiomeCategory.JUNGLE, GenerationStep.Decoration.VEGETAL_DECORATION, getWaystoneFeature(WorldGenStyle.MOSSY));
        addFeatureToBiomes(it -> it.getBiomeCategory() == Biome.BiomeCategory.SWAMP, GenerationStep.Decoration.VEGETAL_DECORATION, getWaystoneFeature(WorldGenStyle.MOSSY));
        addFeatureToBiomes(it -> it.getBiomeCategory() == Biome.BiomeCategory.MUSHROOM, GenerationStep.Decoration.VEGETAL_DECORATION, getWaystoneFeature(WorldGenStyle.MOSSY));
        addFeatureToBiomes(it -> it.getBiomeCategory() != Biome.BiomeCategory.SWAMP
                && it.getBiomeCategory() != Biome.BiomeCategory.DESERT
                && it.getBiomeCategory() != Biome.BiomeCategory.JUNGLE
                && it.getBiomeCategory() != Biome.BiomeCategory.MUSHROOM, GenerationStep.Decoration.VEGETAL_DECORATION, getWaystoneFeature(WorldGenStyle.DEFAULT));
    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(Waystones.MOD_ID, name);
    }

    private static ResourceLocation getWaystoneFeature(WorldGenStyle biomeWorldGenStyle) {
        WorldGenStyle worldGenStyle = WaystonesConfig.getActive().worldGenStyle();
        switch (worldGenStyle) {
            case MOSSY:
                return configuredMossyWaystoneFeature.getIdentifier();
            case SANDY:
                return configuredSandyWaystoneFeature.getIdentifier();
            case BIOME:
                switch (biomeWorldGenStyle) {
                    case SANDY:
                        return configuredSandyWaystoneFeature.getIdentifier();
                    case MOSSY:
                        return configuredMossyWaystoneFeature.getIdentifier();
                    default:
                        return configuredWaystoneFeature.getIdentifier();
                }
            default:
                return configuredWaystoneFeature.getIdentifier();
        }
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
            addWaystoneStructureToVillageConfig(registryAccess, "repurposed_structures:village/badlands/houses", desertVillageWaystoneStructure, 1);
            addWaystoneStructureToVillageConfig(registryAccess, "repurposed_structures:village/birch/houses", villageWaystoneStructure, 1);
            addWaystoneStructureToVillageConfig(registryAccess, "repurposed_structures:village/dark_forest/houses", villageWaystoneStructure, 1);
            addWaystoneStructureToVillageConfig(registryAccess, "repurposed_structures:village/jungle/houses", villageWaystoneStructure, 1);
            addWaystoneStructureToVillageConfig(registryAccess, "repurposed_structures:village/mountains/houses", villageWaystoneStructure, 1);
            addWaystoneStructureToVillageConfig(registryAccess, "repurposed_structures:village/oak/houses", villageWaystoneStructure, 1);
            addWaystoneStructureToVillageConfig(registryAccess, "repurposed_structures:village/swamp/houses", villageWaystoneStructure, 1);
        }
    }

    private static void addWaystoneStructureToVillageConfig(RegistryAccess registryAccess, String villagePiece, ResourceLocation waystoneStructure, int weight) {
        LegacySinglePoolElement piece = StructurePoolElement.legacy(waystoneStructure.toString()).apply(StructureTemplatePool.Projection.RIGID);
        StructureTemplatePool pool = registryAccess.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY).getOptional(new ResourceLocation(villagePiece)).orElse(null);
        if (pool != null) {
            // pretty sure this can be an immutable list (when datapacked) so gotta make a copy to be safe.
            List<StructurePoolElement> listOfPieces = new ArrayList<>(((StructureTemplatePoolAccessor) pool).getTemplates());
            for (int i = 0; i < weight; i++) {
                listOfPieces.add(piece);
            }
            ((StructureTemplatePoolAccessor) pool).setTemplates(listOfPieces);
        }
    }
}
