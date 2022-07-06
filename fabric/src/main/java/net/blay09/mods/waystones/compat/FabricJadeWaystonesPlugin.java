package net.blay09.mods.waystones.compat;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.WaystoneBlockBase;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

@WailaPlugin(Waystones.MOD_ID)
public class FabricJadeWaystonesPlugin implements IWailaPlugin {

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(new WaystoneDataProvider(), WaystoneBlockBase.class);
    }

    private static class WaystoneDataProvider implements IBlockComponentProvider {
        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            JadeWaystones.appendTooltip(accessor.getBlockEntity(), accessor.getPlayer(), tooltip::add);
        }

        @Override
        public ResourceLocation getUid() {
            return JadeWaystones.WAYSTONE_UID;
        }
    }
}
