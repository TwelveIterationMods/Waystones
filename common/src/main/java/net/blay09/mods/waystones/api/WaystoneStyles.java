package net.blay09.mods.waystones.api;

import net.minecraft.resources.ResourceLocation;

public class WaystoneStyles {
    public static WaystoneStyle DEFAULT = new WaystoneStyle(ResourceLocation.fromNamespaceAndPath("waystones", "waystone"));
    public static WaystoneStyle MOSSY = new WaystoneStyle(ResourceLocation.fromNamespaceAndPath("waystones", "mossy_waystone"));
    public static WaystoneStyle SANDY = new WaystoneStyle(ResourceLocation.fromNamespaceAndPath("waystones", "sandy_waystone"));
    public static WaystoneStyle BLACKSTONE = new WaystoneStyle(ResourceLocation.fromNamespaceAndPath("waystones", "blackstone_waystone"));
    public static WaystoneStyle DEEPSLATE = new WaystoneStyle(ResourceLocation.fromNamespaceAndPath("waystones", "deepslate_waystone"));
    public static WaystoneStyle END_STONE = new WaystoneStyle(ResourceLocation.fromNamespaceAndPath("waystones", "end_stone_waystone"));
}
