package net.blay09.mods.waystones.worldgen;

import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.registries.IForgeRegistry;

public class ModFeatures {
    public static WaystoneFeature waystone;

    public static void register(IForgeRegistry<Feature<?>> registry) {
        registry.registerAll(
                waystone = (WaystoneFeature) new WaystoneFeature(NoFeatureConfig::deserialize).setRegistryName("waystone")
        );
    }

}
