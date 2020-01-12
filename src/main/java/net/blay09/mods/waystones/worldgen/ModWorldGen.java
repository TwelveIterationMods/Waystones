package net.blay09.mods.waystones.worldgen;

import net.blay09.mods.waystones.config.WaystoneConfig;
import net.blay09.mods.waystones.block.ModBlocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.SingleJigsawPiece;
import net.minecraft.world.gen.feature.structure.*;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Collections;
import java.util.Objects;

public class ModWorldGen {
    private static final ResourceLocation villageWaystoneStructure = new ResourceLocation("waystones", "village/common/waystone");
    private static final ResourceLocation desertVillageWaystoneStructure = new ResourceLocation("waystones", "village/desert/waystone");
    private static final ResourceLocation emptyStructure = new ResourceLocation("empty");

    private static WaystoneFeature mossyWaystoneFeature;
    private static WaystoneFeature sandyWaystoneFeature;
    private static WaystonePlacement waystonePlacement;

    public static void registerFeatures(IForgeRegistry<Feature<?>> registry) {
        registry.registerAll(
                new WaystoneFeature(NoFeatureConfig::deserialize, ModBlocks.waystone.getDefaultState()).setRegistryName("waystone"),
                mossyWaystoneFeature = (WaystoneFeature) new WaystoneFeature(NoFeatureConfig::deserialize, ModBlocks.mossyWaystone.getDefaultState()).setRegistryName("mossy_waystone"),
                sandyWaystoneFeature = (WaystoneFeature) new WaystoneFeature(NoFeatureConfig::deserialize, ModBlocks.sandyWaystone.getDefaultState()).setRegistryName("sandy_waystone")
        );
    }

    public static void registerPlacements(IForgeRegistry<Placement<?>> registry) {
        registry.registerAll(
                waystonePlacement = (WaystonePlacement) new WaystonePlacement(NoPlacementConfig::deserialize).setRegistryName("waystone")
        );
    }

    public static void setupRandomWorldGen() {
        if (WaystoneConfig.COMMON.worldGenFrequency.get() > 0) {
            Biome.BIOMES.forEach(it -> {
                boolean isDesert = Objects.requireNonNull(it.getRegistryName()).getPath().contains("desert");
                WaystoneFeature biomeWaystoneFeature = isDesert ? sandyWaystoneFeature : mossyWaystoneFeature;
                ConfiguredFeature<?> configuredFeature = Biome.createDecoratedFeature(biomeWaystoneFeature, NoFeatureConfig.NO_FEATURE_CONFIG, waystonePlacement, NoPlacementConfig.NO_PLACEMENT_CONFIG);
                it.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, configuredFeature);
            });
        }
    }

    public static void setupVillageWorldGen() {
        JigsawManager.REGISTRY.register(new JigsawPattern(villageWaystoneStructure, emptyStructure, Collections.emptyList(), JigsawPattern.PlacementBehaviour.RIGID));
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
        }
    }

    private static void addWaystoneStructureToVillageConfig(String villagePiece, ResourceLocation waystoneStructure) {
        JigsawPattern houses = JigsawManager.REGISTRY.get(new ResourceLocation(villagePiece));

        houses.jigsawPieces.add(new SingleJigsawPiece(waystoneStructure.toString(), Collections.emptyList(), JigsawPattern.PlacementBehaviour.RIGID));
    }
}
