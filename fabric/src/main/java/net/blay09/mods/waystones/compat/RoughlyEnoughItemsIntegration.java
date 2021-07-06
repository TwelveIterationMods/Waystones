package net.blay09.mods.waystones.compat;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.screen.OverlayDecider;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import net.blay09.mods.waystones.client.gui.screen.WaystoneSelectionScreen;
import net.blay09.mods.waystones.client.gui.screen.WaystoneSettingsScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.InteractionResult;

@Environment(EnvType.CLIENT)
public class RoughlyEnoughItemsIntegration implements REIClientPlugin {

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerDecider(new OverlayDecider() {
            @Override
            public <R extends Screen> boolean isHandingScreen(Class<R> aClass) {
                return WaystoneSelectionScreen.class == aClass || WaystoneSettingsScreen.class == aClass;
            }

            @Override
            public InteractionResult shouldScreenBeOverlaid(Class<?> screen) {
                return InteractionResult.FAIL;
            }
        });
    }

}
