package net.blay09.mods.waystones.client;

import net.blay09.mods.waystones.client.render.WaystoneRenderer;
import net.blay09.mods.waystones.tileentity.WaystoneTileEntity;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ModRenderers {
    public static void registerRenderers() {
        ClientRegistry.bindTileEntitySpecialRenderer(WaystoneTileEntity.class, new WaystoneRenderer());
    }
}
