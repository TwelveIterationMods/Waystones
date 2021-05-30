package net.blay09.mods.waystones.client;

import net.blay09.mods.waystones.Waystones;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Waystones.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModTextures {

    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        if (event.getMap().getTextureLocation().equals(Atlases.SIGN_ATLAS)) {
            event.addSprite(new ResourceLocation(Waystones.MOD_ID, "entity/waystone_active"));
            event.addSprite(new ResourceLocation(Waystones.MOD_ID, "entity/sharestone_color"));
            event.addSprite(new ResourceLocation(Waystones.MOD_ID, "entity/portstone"));
        }
    }
}
