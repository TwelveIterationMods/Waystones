package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.Waystones;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class WaystoneTypes {
    public static final ResourceLocation WAYSTONE = new ResourceLocation(Waystones.MOD_ID, "waystone");
    public static final ResourceLocation WARP_PLATE = new ResourceLocation(Waystones.MOD_ID, "warp_plate");
    public static final ResourceLocation PORTSTONE = new ResourceLocation(Waystones.MOD_ID, "portstone");

    private static final ResourceLocation SHARESTONE = new ResourceLocation(Waystones.MOD_ID, "sharestone");

    public static ResourceLocation getSharestone(@Nullable DyeColor color) {
        if (color == null) {
            return SHARESTONE;
        }

        return new ResourceLocation(Waystones.MOD_ID, color.getTranslationKey() + "_sharestone");
    }

    public static boolean isSharestone(ResourceLocation waystoneType) {
        return waystoneType.equals(SHARESTONE) || waystoneType.getPath().endsWith("_sharestone");
    }
}
