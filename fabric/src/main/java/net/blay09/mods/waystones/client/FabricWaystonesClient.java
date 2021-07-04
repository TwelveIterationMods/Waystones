package net.blay09.mods.waystones.client;

import net.fabricmc.api.ClientModInitializer;

public class FabricWaystonesClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        WaystonesClient.initialize();
    }
}
