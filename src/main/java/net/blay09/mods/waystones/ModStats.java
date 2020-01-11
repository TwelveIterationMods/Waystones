package net.blay09.mods.waystones;

import net.minecraft.stats.IStatFormatter;
import net.minecraft.stats.Stats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class ModStats {

    public static final ResourceLocation waystoneActivated = new ResourceLocation("waystones", "waystone_activated");

    public static void registerStats() {
        Registry.register(Registry.CUSTOM_STAT, waystoneActivated.getPath(), waystoneActivated);
        Stats.CUSTOM.get(waystoneActivated, IStatFormatter.DEFAULT);
    }

}
