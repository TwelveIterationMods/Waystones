package net.blay09.mods.waystones.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.blay09.mods.balm.fabric.config.FabricBalmConfig;
import net.blay09.mods.waystones.config.WaystonesConfigData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return FabricBalmConfig.getConfigScreen(WaystonesConfigData.class);
    }
}
