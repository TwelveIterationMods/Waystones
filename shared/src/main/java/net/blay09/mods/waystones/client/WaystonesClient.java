package net.blay09.mods.waystones.client;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.compat.Compat;
import net.blay09.mods.waystones.compat.XaerosMinimapAddon;
import net.minecraft.client.ClientBrandRetriever;

import java.util.Locale;

public class WaystonesClient {
    public static void initialize() {
        BalmClient.initialize(Waystones.MOD_ID);

        ModClientEventHandlers.initialize();
        ModRenderers.initialize(BalmClient.getRenderers());
        ModScreens.initialize(BalmClient.getScreens());
        ModTextures.initialize(BalmClient.getTextures());

        InventoryButtonGuiHandler.initialize();

        Compat.isVivecraftInstalled = ClientBrandRetriever.getClientModName().toLowerCase(Locale.ENGLISH).contains(Compat.VIVECRAFT);

        Compat.isXaerosMinimapInstalled = Balm.isModLoaded(Compat.XAEROS);
        if (Compat.isXaerosMinimapInstalled) {
            XaerosMinimapAddon.initialize();
        }
    }
}
