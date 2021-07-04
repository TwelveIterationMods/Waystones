package net.blay09.mods.waystones;

import net.fabricmc.api.ModInitializer;

public class FabricWaystones implements ModInitializer {
    @Override
    public void onInitialize() {
        Waystones.initialize();
    }
}
