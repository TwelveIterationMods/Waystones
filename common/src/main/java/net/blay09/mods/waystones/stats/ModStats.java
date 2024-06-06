package net.blay09.mods.waystones.stats;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.Waystones;
import net.minecraft.resources.ResourceLocation;

public class ModStats {

    public static final ResourceLocation waystoneActivated = ResourceLocation.fromNamespaceAndPath(Waystones.MOD_ID, "waystone_activated");

    public static void initialize() {
        Balm.getStats().registerCustomStat(waystoneActivated);
    }

}
