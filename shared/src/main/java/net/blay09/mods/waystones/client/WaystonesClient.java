package net.blay09.mods.waystones.client;

import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.waystones.InternalClientMethodsImpl;
import net.blay09.mods.waystones.api.client.WaystonesClientAPI;
import net.blay09.mods.waystones.client.requirement.RequirementClientRegistry;
import net.blay09.mods.waystones.compat.Compat;
import net.minecraft.client.ClientBrandRetriever;

import java.util.Locale;

public class WaystonesClient {
    public static void initialize() {
        WaystonesClientAPI.__internalMethods = new InternalClientMethodsImpl();
        RequirementClientRegistry.registerDefaults();

        ModClientEventHandlers.initialize();
        ModRenderers.initialize(BalmClient.getRenderers());
        ModScreens.initialize(BalmClient.getScreens());
        ModTextures.initialize(BalmClient.getTextures());

        InventoryButtonGuiHandler.initialize();

        Compat.isVivecraftInstalled = ClientBrandRetriever.getClientModName().toLowerCase(Locale.ENGLISH).contains(Compat.VIVECRAFT);
    }
}
