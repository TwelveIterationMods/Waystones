package net.blay09.mods.waystones.api;

import net.blay09.mods.waystones.Waystones;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.Nullable;

public class WaystoneTypes {
    public static final ResourceLocation WAYSTONE = new ResourceLocation(Waystones.MOD_ID, "waystone");
    public static final ResourceLocation WARP_PLATE = new ResourceLocation(Waystones.MOD_ID, "warp_plate");
    public static final ResourceLocation PORTSTONE = new ResourceLocation(Waystones.MOD_ID, "portstone");
    public static final ResourceLocation LANDING_STONE = new ResourceLocation(Waystones.MOD_ID, "landing_stone");

    public static final ResourceLocation SHARESTONE = new ResourceLocation(Waystones.MOD_ID, "sharestone");
    public static final ResourceLocation[] DYED_SHARESTONES = new ResourceLocation[] {
            new ResourceLocation(Waystones.MOD_ID, "white_sharestone"),
            new ResourceLocation(Waystones.MOD_ID, "orange_sharestone"),
            new ResourceLocation(Waystones.MOD_ID, "magenta_sharestone"),
            new ResourceLocation(Waystones.MOD_ID, "light_blue_sharestone"),
            new ResourceLocation(Waystones.MOD_ID, "yellow_sharestone"),
            new ResourceLocation(Waystones.MOD_ID, "lime_sharestone"),
            new ResourceLocation(Waystones.MOD_ID, "pink_sharestone"),
            new ResourceLocation(Waystones.MOD_ID, "gray_sharestone"),
            new ResourceLocation(Waystones.MOD_ID, "light_gray_sharestone"),
            new ResourceLocation(Waystones.MOD_ID, "cyan_sharestone"),
            new ResourceLocation(Waystones.MOD_ID, "purple_sharestone"),
            new ResourceLocation(Waystones.MOD_ID, "blue_sharestone"),
            new ResourceLocation(Waystones.MOD_ID, "brown_sharestone"),
            new ResourceLocation(Waystones.MOD_ID, "green_sharestone"),
            new ResourceLocation(Waystones.MOD_ID, "red_sharestone"),
            new ResourceLocation(Waystones.MOD_ID, "black_sharestone")
    };

    public static ResourceLocation getSharestone(@Nullable DyeColor color) {
        if (color == null) {
            return SHARESTONE;
        }

        return new ResourceLocation(Waystones.MOD_ID, color.getSerializedName() + "_sharestone");
    }

    public static boolean isSharestone(ResourceLocation waystoneType) {
        return waystoneType.equals(SHARESTONE) || waystoneType.getPath().endsWith("_sharestone");
    }
}
