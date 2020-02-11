package net.blay09.mods.waystones.client;

import net.blay09.mods.waystones.client.render.WaystoneRenderer;
import net.blay09.mods.waystones.tileentity.ModTileEntities;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ModRenderers {
    public static void registerRenderers() {
        ClientRegistry.bindTileEntityRenderer(ModTileEntities.waystone, WaystoneRenderer::new);
    }
}
