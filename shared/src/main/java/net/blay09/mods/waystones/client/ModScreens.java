package net.blay09.mods.waystones.client;

import net.blay09.mods.balm.api.client.screen.BalmScreens;
import net.blay09.mods.waystones.client.gui.screen.*;
import net.blay09.mods.waystones.menu.ModMenus;

public class ModScreens {
    public static void initialize(BalmScreens screens) {
        screens.registerScreen(ModMenus.waystoneSelection::get, WaystoneSelectionScreen::new);
        screens.registerScreen(ModMenus.warpScrollSelection::get, WaystoneSelectionScreen::new);
        screens.registerScreen(ModMenus.warpStoneSelection::get, WaystoneSelectionScreen::new);
        screens.registerScreen(ModMenus.portstoneSelection::get, WaystoneSelectionScreen::new);
        screens.registerScreen(ModMenus.inventorySelection::get, WaystoneSelectionScreen::new);
        screens.registerScreen(ModMenus.sharestoneSelection::get, SharestoneSelectionScreen::new);
        screens.registerScreen(ModMenus.warpPlate::get, WarpPlateScreen::new);
        screens.registerScreen(ModMenus.waystoneSettings::get, WaystoneScreen::new);
        screens.registerScreen(ModMenus.adminSelection::get, AdminSelectionScreen::new);
    }
}
