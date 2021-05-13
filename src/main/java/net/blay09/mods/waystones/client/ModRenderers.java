package net.blay09.mods.waystones.client;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.block.SharestoneBlock;
import net.blay09.mods.waystones.client.render.SharestoneRenderer;
import net.blay09.mods.waystones.client.render.WaystoneRenderer;
import net.blay09.mods.waystones.tileentity.ModTileEntities;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = Waystones.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModRenderers {
    public static void registerRenderers() {
        ClientRegistry.bindTileEntityRenderer(ModTileEntities.waystone, WaystoneRenderer::new);
        ClientRegistry.bindTileEntityRenderer(ModTileEntities.sharestone, SharestoneRenderer::new);

        RenderTypeLookup.setRenderLayer(ModBlocks.sharestone, RenderType.getCutout());
    }

    @SubscribeEvent
    public static void initBlockColors(ColorHandlerEvent.Item event) {
        // Check for null in case event bus crashed
        if (ModBlocks.sharestone != null) {
            for (SharestoneBlock scopedSharestone : ModBlocks.scopedSharestones) {
                event.getItemColors().register((itemStack, tintIndex) -> Objects.requireNonNull(scopedSharestone.getColor()).getColorValue(), scopedSharestone);
            }
        }
    }
}
