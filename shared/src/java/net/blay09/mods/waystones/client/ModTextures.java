package net.blay09.mods.waystones.client;

import net.blay09.mods.balm.client.rendering.BalmTextures;
import net.blay09.mods.waystones.Waystones;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.resources.ResourceLocation;

public class ModTextures extends BalmTextures {

    public static void initialize() {
        addSprite(Sheets.SIGN_SHEET, new ResourceLocation(Waystones.MOD_ID, "entity/waystone_active"));
        addSprite(Sheets.SIGN_SHEET, new ResourceLocation(Waystones.MOD_ID, "entity/sharestone_color"));
        addSprite(Sheets.SIGN_SHEET, new ResourceLocation(Waystones.MOD_ID, "entity/portstone"));
    }

}
