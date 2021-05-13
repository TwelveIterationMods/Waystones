package net.blay09.mods.waystones.client;

import net.blay09.mods.waystones.client.gui.screen.SharestoneSelectionScreen;
import net.blay09.mods.waystones.client.gui.screen.WarpPlateScreen;
import net.blay09.mods.waystones.client.gui.screen.WaystoneSelectionScreen;
import net.blay09.mods.waystones.client.gui.screen.WaystoneSettingsScreen;
import net.blay09.mods.waystones.container.ModContainers;
import net.minecraft.client.gui.ScreenManager;

public class ModScreens {
    public static void registerScreens() {
        ScreenManager.registerFactory(ModContainers.waystoneSelection, WaystoneSelectionScreen::new);
        ScreenManager.registerFactory(ModContainers.sharestoneSelection, SharestoneSelectionScreen::new);
        ScreenManager.registerFactory(ModContainers.warpPlate, WarpPlateScreen::new);
        ScreenManager.registerFactory(ModContainers.waystoneSettings, WaystoneSettingsScreen::new);
    }
}
