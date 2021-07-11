package net.blay09.mods.waystones.client;

import net.blay09.mods.balm.client.screen.BalmScreens;
import net.blay09.mods.waystones.client.gui.screen.SharestoneSelectionScreen;
import net.blay09.mods.waystones.client.gui.screen.WarpPlateScreen;
import net.blay09.mods.waystones.client.gui.screen.WaystoneSelectionScreen;
import net.blay09.mods.waystones.client.gui.screen.WaystoneSettingsScreen;
import net.blay09.mods.waystones.menu.ModMenus;

public class ModScreens extends BalmScreens {
    public static void initialize() {
        registerScreen(ModMenus.waystoneSelection.get(), WaystoneSelectionScreen::new);
        registerScreen(ModMenus.sharestoneSelection.get(), SharestoneSelectionScreen::new);
        registerScreen(ModMenus.warpPlate.get(), WarpPlateScreen::new);
        registerScreen(ModMenus.waystoneSettings.get(), WaystoneSettingsScreen::new);
    }
}
