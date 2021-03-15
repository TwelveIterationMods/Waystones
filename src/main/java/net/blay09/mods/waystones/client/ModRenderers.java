package net.blay09.mods.waystones.client;

import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.client.render.SharestoneRenderer;
import net.blay09.mods.waystones.client.render.WaystoneRenderer;
import net.blay09.mods.waystones.tileentity.ModTileEntities;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ModRenderers {
    public static void registerRenderers() {
        ClientRegistry.bindTileEntityRenderer(ModTileEntities.waystone, WaystoneRenderer::new);
        ClientRegistry.bindTileEntityRenderer(ModTileEntities.sharestone, SharestoneRenderer::new);

        RenderTypeLookup.setRenderLayer(ModBlocks.sharestone, RenderType.getCutout());
    }
}
