package net.blay09.mods.waystones.compat.rei;

import me.shedaniel.rei.api.client.registry.screen.OverlayDecider;
import net.blay09.mods.waystones.client.gui.screen.WaystoneSelectionScreen;
import net.blay09.mods.waystones.client.gui.screen.WaystoneScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.InteractionResult;

class WaystonesOverlayDecider implements OverlayDecider {
    @Override
    public <R extends Screen> boolean isHandingScreen(Class<R> aClass) {
        return WaystoneSelectionScreen.class == aClass || WaystoneScreen.class == aClass;
    }

    @Override
    public InteractionResult shouldScreenBeOverlaid(Screen screen) {
        return InteractionResult.FAIL;
    }
}
