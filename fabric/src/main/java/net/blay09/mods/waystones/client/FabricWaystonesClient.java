package net.blay09.mods.waystones.client;

import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.waystones.Waystones;
import net.fabricmc.api.ClientModInitializer;

public class FabricWaystonesClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BalmClient.initialize(Waystones.MOD_ID, WaystonesClient::initialize);
    }
}
