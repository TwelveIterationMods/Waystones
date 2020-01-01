package net.blay09.mods.waystones.worldgen;

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

public class ModWorldGen {
    private static final ResourceLocation waystoneVillageStructure = new ResourceLocation("waystones", "village_waystone");
    private static final ResourceLocation emptyStructure = new ResourceLocation("empty");

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
        // TODO config needs to be able to disable it (== 0)
        Biome.BIOMES.forEach(it -> {
            ConfiguredFeature<?> configuredFeature = Biome.createDecoratedFeature(waystoneFeature, NoFeatureConfig.NO_FEATURE_CONFIG, waystonePlacement, NoPlacementConfig.NO_PLACEMENT_CONFIG);
            it.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, configuredFeature);
            // TODO Desert waystones: sandy? jungle waystones: mossy? actually we wanted all of them to be mossy but we'll see, maybe not
        });
    }

    public static void setupVillageWorldGen() {
        JigsawManager.REGISTRY.register(new JigsawPattern(waystoneVillageStructure, emptyStructure, Collections.emptyList(), JigsawPattern.PlacementBehaviour.RIGID));

        // TODO Make the waystone in the structure "uninitialized"
        // TODO tie behind config
        // other variants (at least desert)

        PlainsVillagePools.init();
        SnowyVillagePools.init();
        SavannaVillagePools.init();
        DesertVillagePools.init();
        TaigaVillagePools.init();

        addWaystoneStructureToVillageConfig("village/plains/houses");
        addWaystoneStructureToVillageConfig("village/snowy/houses");
        addWaystoneStructureToVillageConfig("village/savanna/houses");
        addWaystoneStructureToVillageConfig("village/desert/houses");
        addWaystoneStructureToVillageConfig("village/taiga/houses");
    }

    private static void addWaystoneStructureToVillageConfig(String villagePiece) {
        JigsawPattern houses = JigsawManager.REGISTRY.get(new ResourceLocation(villagePiece));

        houses.jigsawPieces.add(new SingleJigsawPiece(waystoneVillageStructure.toString(), Collections.emptyList(), JigsawPattern.PlacementBehaviour.RIGID));
    }
}
