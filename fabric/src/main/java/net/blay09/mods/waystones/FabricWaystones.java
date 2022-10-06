package net.blay09.mods.waystones;

import net.blay09.mods.balm.api.Balm;
import net.fabricmc.api.ModInitializer;

public class FabricWaystones implements ModInitializer {
    @Override
    public void onInitialize() {
        Balm.initialize(Waystones.MOD_ID, Waystones::initialize);
    }
}
