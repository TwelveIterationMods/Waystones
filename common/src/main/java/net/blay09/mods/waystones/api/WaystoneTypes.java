package net.blay09.mods.waystones.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class WaystoneTypes {
    public static final ResourceLocation WAYSTONE = new ResourceLocation("waystones", "waystone");
    public static final ResourceLocation WARP_PLATE = new ResourceLocation("waystones", "warp_plate");
    public static final ResourceLocation PORTSTONE = new ResourceLocation("waystones", "portstone");
    public static final ResourceLocation LANDING_STONE = new ResourceLocation("waystones", "landing_stone");

    public static final ResourceLocation[] SHARESTONES = new ResourceLocation[]{
            new ResourceLocation("waystones", "orange_sharestone"),
            new ResourceLocation("waystones", "magenta_sharestone"),
            new ResourceLocation("waystones", "light_blue_sharestone"),
            new ResourceLocation("waystones", "yellow_sharestone"),
            new ResourceLocation("waystones", "lime_sharestone"),
            new ResourceLocation("waystones", "pink_sharestone"),
            new ResourceLocation("waystones", "gray_sharestone"),
            new ResourceLocation("waystones", "light_gray_sharestone"),
            new ResourceLocation("waystones", "cyan_sharestone"),
            new ResourceLocation("waystones", "purple_sharestone"),
            new ResourceLocation("waystones", "blue_sharestone"),
            new ResourceLocation("waystones", "brown_sharestone"),
            new ResourceLocation("waystones", "green_sharestone"),
            new ResourceLocation("waystones", "red_sharestone"),
            new ResourceLocation("waystones", "black_sharestone")
    };

    public static Optional<ResourceLocation> getSharestone(@Nullable DyeColor color) {
        if (color == null || color == DyeColor.WHITE) {
            return Optional.empty();
        }

        return Optional.of(new ResourceLocation("waystones", color.getSerializedName() + "_sharestone"));
    }

    public static boolean isSharestone(ResourceLocation waystoneType) {
        return waystoneType.getPath().endsWith("_sharestone");
    }
}
