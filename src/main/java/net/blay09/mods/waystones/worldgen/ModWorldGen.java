package net.blay09.mods.waystones.worldgen;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Collections;

public class ModWorldGen {
    private static WaystoneFeature waystoneFeature;
    private static WaystonePlacement waystonePlacement;

    public static void registerFeatures(IForgeRegistry<Feature<?>> registry) {
        registry.registerAll(
                waystoneFeature = (WaystoneFeature) new WaystoneFeature(NoFeatureConfig::deserialize).setRegistryName("waystone")
        );
    }

    public static void registerPlacements(IForgeRegistry<Placement<?>> registry) {
        registry.registerAll(
                waystonePlacement = (WaystonePlacement) new WaystonePlacement(NoPlacementConfig::deserialize).setRegistryName("waystone")
        );
    }

    public static void setupRandomWorldGen() {
        Biome.BIOMES.forEach(it -> {
            ConfiguredFeature<?> configuredFeature = Biome.createDecoratedFeature(waystoneFeature, NoFeatureConfig.NO_FEATURE_CONFIG, waystonePlacement, NoPlacementConfig.NO_PLACEMENT_CONFIG);
            it.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, configuredFeature);
        });
    }

    public static void setupVillageWorldGen() {
        ResourceLocation villageStructure = new ResourceLocation("waystones", "village_waystone");
        ResourceLocation emptyStructure = new ResourceLocation("empty");
        JigsawManager.REGISTRY.register(new JigsawPattern(villageStructure, emptyStructure, Collections.emptyList(), JigsawPattern.PlacementBehaviour.RIGID));
    }
}
