package net.blay09.mods.waystones.worldgen;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.config.WorldGenStyle;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

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

    public static void setupVillageWorldGen() {
        /*JigsawManager.REGISTRY.register(new JigsawPattern(villageWaystoneStructure, emptyStructure, Collections.emptyList(), JigsawPattern.PlacementBehaviour.RIGID));
        JigsawManager.REGISTRY.register(new JigsawPattern(desertVillageWaystoneStructure, emptyStructure, Collections.emptyList(), JigsawPattern.PlacementBehaviour.RIGID));

        if (WaystoneConfig.COMMON.addVillageStructure.get()) {
            PlainsVillagePools.init();
            SnowyVillagePools.init();
            SavannaVillagePools.init();
            DesertVillagePools.init();
            TaigaVillagePools.init();

            addWaystoneStructureToVillageConfig("village/plains/houses", villageWaystoneStructure);
            addWaystoneStructureToVillageConfig("village/snowy/houses", villageWaystoneStructure);
            addWaystoneStructureToVillageConfig("village/savanna/houses", villageWaystoneStructure);
            addWaystoneStructureToVillageConfig("village/desert/houses", desertVillageWaystoneStructure);
            addWaystoneStructureToVillageConfig("village/taiga/houses", villageWaystoneStructure);
        }*/
    }

    private static void addWaystoneStructureToVillageConfig(String villagePiece, ResourceLocation waystoneStructure) {
        /*JigsawPattern houses = JigsawManager.REGISTRY.get(new ResourceLocation(villagePiece));

        final SingleJigsawPiece piece = new SingleJigsawPiece(waystoneStructure.toString());
        houses.rawTemplates = ImmutableList.<Pair<JigsawPiece, Integer>>builder().addAll(houses.rawTemplates).add(Pair.of(piece, 1)).build();
        houses.jigsawPieces.add(piece);*/
    }
}
