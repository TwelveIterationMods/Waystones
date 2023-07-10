package net.blay09.mods.waystones.worldgen;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
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
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.structure.pools.LegacySinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModWorldGen {
    private static final ResourceLocation villageWaystoneStructure = new ResourceLocation("waystones", "village/common/waystone");
    private static final ResourceLocation desertVillageWaystoneStructure = new ResourceLocation("waystones", "village/desert/waystone");
    private static final ResourceKey<StructureProcessorList> EMPTY_PROCESSOR_LIST_KEY = ResourceKey.create(Registry.PROCESSOR_LIST_REGISTRY, new ResourceLocation("minecraft", "empty"));

    private static DeferredObject<WaystoneFeature> waystoneFeature;
    private static DeferredObject<WaystoneFeature> mossyWaystoneFeature;
    private static DeferredObject<WaystoneFeature> sandyWaystoneFeature;
    private static DeferredObject<ConfiguredFeature<NoneFeatureConfiguration, WaystoneFeature>> configuredWaystoneFeature;
    private static DeferredObject<ConfiguredFeature<NoneFeatureConfiguration, WaystoneFeature>> configuredMossyWaystoneFeature;
    private static DeferredObject<ConfiguredFeature<NoneFeatureConfiguration, WaystoneFeature>> configuredSandyWaystoneFeature;
    private static DeferredObject<PlacedFeature> placedWaystoneFeature;
    private static DeferredObject<PlacedFeature> placedMossyWaystoneFeature;
    private static DeferredObject<PlacedFeature> placedSandyWaystoneFeature;
    public static DeferredObject<PlacementModifierType<WaystonePlacement>> waystonePlacement;

    public static void initialize(BalmWorldGen worldGen) {
        waystoneFeature = worldGen.registerFeature(id("waystone"), () -> new WaystoneFeature(NoneFeatureConfiguration.CODEC, ModBlocks.waystone.defaultBlockState()));
        mossyWaystoneFeature = worldGen.registerFeature(id("mossy_waystone"), () -> new WaystoneFeature(NoneFeatureConfiguration.CODEC, ModBlocks.mossyWaystone.defaultBlockState()));
        sandyWaystoneFeature = worldGen.registerFeature(id("sandy_waystone"), () -> new WaystoneFeature(NoneFeatureConfiguration.CODEC, ModBlocks.sandyWaystone.defaultBlockState()));

        waystonePlacement = worldGen.registerPlacementModifier(id("waystone"), () -> () -> WaystonePlacement.CODEC);

        configuredWaystoneFeature = worldGen.registerConfiguredFeature(id("waystone"), waystoneFeature::get, () -> FeatureConfiguration.NONE);
        configuredMossyWaystoneFeature = worldGen.registerConfiguredFeature(id("mossy_waystone"), mossyWaystoneFeature::get, () -> FeatureConfiguration.NONE);
        configuredSandyWaystoneFeature = worldGen.registerConfiguredFeature(id("sandy_waystone"), sandyWaystoneFeature::get, () -> FeatureConfiguration.NONE);

        placedWaystoneFeature = worldGen.registerPlacedFeature(id("waystone"), configuredWaystoneFeature::get, new WaystonePlacement());
        placedMossyWaystoneFeature = worldGen.registerPlacedFeature(id("mossy_waystone"), configuredMossyWaystoneFeature::get, new WaystonePlacement());
        placedSandyWaystoneFeature = worldGen.registerPlacedFeature(id("sandy_waystone"), configuredSandyWaystoneFeature::get, new WaystonePlacement());

        final var IS_DESERT = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation("waystones", "is_desert"));
        final var IS_SWAMP = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation("waystones", "is_swamp"));
        final var IS_MUSHROOM = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation("waystones", "is_mushroom"));
        worldGen.addFeatureToBiomes(matchesTag(IS_DESERT), GenerationStep.Decoration.VEGETAL_DECORATION, getWaystoneFeature(WorldGenStyle.SANDY));
        worldGen.addFeatureToBiomes(matchesTag(BiomeTags.IS_JUNGLE), GenerationStep.Decoration.VEGETAL_DECORATION, getWaystoneFeature(WorldGenStyle.MOSSY));
        worldGen.addFeatureToBiomes(matchesTag(IS_SWAMP), GenerationStep.Decoration.VEGETAL_DECORATION, getWaystoneFeature(WorldGenStyle.MOSSY));
        worldGen.addFeatureToBiomes(matchesTag(IS_MUSHROOM), GenerationStep.Decoration.VEGETAL_DECORATION, getWaystoneFeature(WorldGenStyle.MOSSY));
        worldGen.addFeatureToBiomes(matchesNeitherTag(List.of(IS_SWAMP, IS_DESERT, BiomeTags.IS_JUNGLE, IS_MUSHROOM)), GenerationStep.Decoration.VEGETAL_DECORATION, getWaystoneFeature(WorldGenStyle.DEFAULT));

        Balm.getEvents().onEvent(ServerStartedEvent.class, event -> setupVillageWorldGen(event.getServer().registryAccess()));
        Balm.getEvents().onEvent(ServerReloadedEvent.class, event -> setupVillageWorldGen(event.getServer().registryAccess()));

        // Registers a condition for repurposed structures compat
        Registry.REGISTRY.getOptional(new ResourceLocation("repurposed_structures", "json_conditions"))
                .ifPresent(registry -> Registry.register(
                        (Registry<Supplier<Boolean>>) registry,
                        new ResourceLocation("waystones", "config"),
                        () -> WaystonesConfig.getActive().spawnInVillages() || WaystonesConfig.getActive().forceSpawnInVillages()));
    }

    private static BiomePredicate matchesTag(TagKey<Biome> tag) {
        return (resourceLocation, biome) -> biome.is(tag);
    }

    private static BiomePredicate matchesNeitherTag(List<TagKey<Biome>> tags) {
        return (resourceLocation, biome) -> {
            for (TagKey<Biome> tag : tags) {
                if (biome.is(tag)) {
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
    }

    private static void addWaystoneStructureToVillageConfig(RegistryAccess registryAccess, String villagePiece, ResourceLocation waystoneStructure, int weight) {

        Holder<StructureProcessorList> emptyProcessorList = registryAccess.registryOrThrow(Registry.PROCESSOR_LIST_REGISTRY).getHolderOrThrow(EMPTY_PROCESSOR_LIST_KEY);
        LegacySinglePoolElement piece = StructurePoolElement.legacy(waystoneStructure.toString(), emptyProcessorList).apply(StructureTemplatePool.Projection.RIGID);
        if (piece instanceof WaystoneStructurePoolElement element) {
            element.setIsWaystone(true);
        }
        StructureTemplatePool pool = registryAccess.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY).getOptional(new ResourceLocation(villagePiece)).orElse(null);
        if (pool != null) {
            var poolAccessor = (StructureTemplatePoolAccessor) pool;
            // pretty sure this can be an immutable list (when datapacked) so gotta make a copy to be safe.
            final var listOfPieces = new ObjectArrayList<>(poolAccessor.getTemplates());
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
