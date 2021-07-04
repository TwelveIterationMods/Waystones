package net.blay09.mods.waystones.client;

import net.blay09.mods.forbic.client.ForbicModScreens;
import net.blay09.mods.waystones.client.gui.screen.SharestoneSelectionScreen;
import net.blay09.mods.waystones.client.gui.screen.WarpPlateScreen;
import net.blay09.mods.waystones.client.gui.screen.WaystoneSelectionScreen;
import net.blay09.mods.waystones.client.gui.screen.WaystoneSettingsScreen;
import net.blay09.mods.waystones.menu.ModMenus;

public class ModScreens extends ForbicModScreens {
    public static void initialize() {
        register(ModMenus.waystoneSelection.get(), WaystoneSelectionScreen::new);
        register(ModMenus.sharestoneSelection.get(), SharestoneSelectionScreen::new);
        register(ModMenus.warpPlate.get(), WarpPlateScreen::new);
        register(ModMenus.waystoneSettings.get(), WaystoneSettingsScreen::new);
    }
}
