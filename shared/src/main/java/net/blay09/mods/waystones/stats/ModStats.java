package net.blay09.mods.waystones.stats;

import net.blay09.mods.waystones.Waystones;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;

public class ModStats {

    public static final ResourceLocation waystoneActivated = new ResourceLocation(Waystones.MOD_ID, "waystone_activated");

    public static void initialize() {
        Registry.register(Registry.CUSTOM_STAT, waystoneActivated.getPath(), waystoneActivated);
        Stats.CUSTOM.get(waystoneActivated, StatFormatter.DEFAULT);
    }

}
