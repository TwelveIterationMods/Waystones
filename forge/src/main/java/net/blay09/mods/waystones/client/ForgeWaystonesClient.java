package net.blay09.mods.waystones.client;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ForgeWaystonesClient {
    public static void setupClient(FMLClientSetupEvent event) {
        WaystonesClient.initialize();
    }
}
