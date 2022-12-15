package net.blay09.mods.waystones.compat.rei;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;

public class RoughlyEnoughItemsIntegration implements REIClientPlugin {

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerDecider(new WaystonesOverlayDecider());
    }

}
