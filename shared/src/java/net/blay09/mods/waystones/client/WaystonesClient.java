package net.blay09.mods.waystones.client;

import net.blay09.mods.waystones.compat.Compat;
import net.minecraft.client.ClientBrandRetriever;

import java.util.Locale;

public class WaystonesClient {
    public static void initialize() {
        ModRenderers.initialize();
        ModScreens.initialize();
        ModTextures.initialize();

        InventoryButtonGuiHandler.initialize();

        Compat.isVivecraftInstalled = ClientBrandRetriever.getClientModName().toLowerCase(Locale.ENGLISH).contains(Compat.VIVECRAFT);
    }
}
