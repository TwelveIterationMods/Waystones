package net.blay09.mods.waystones.client;

import net.blay09.mods.balm.api.client.rendering.BalmTextures;
import net.blay09.mods.waystones.Waystones;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.resources.ResourceLocation;

public class ModTextures {

    public static void initialize(BalmTextures textures) {
        textures.addSprite(Sheets.SIGN_SHEET, new ResourceLocation(Waystones.MOD_ID, "entity/waystone_active"));
        textures.addSprite(Sheets.SIGN_SHEET, new ResourceLocation(Waystones.MOD_ID, "entity/sharestone_color"));
        textures.addSprite(Sheets.SIGN_SHEET, new ResourceLocation(Waystones.MOD_ID, "entity/portstone"));
    }

}
