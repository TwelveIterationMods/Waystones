package net.blay09.mods.waystones.tag;

import net.blay09.mods.waystones.Waystones;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {

    public static final TagKey<Item> BOUND_SCROLLS = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Waystones.MOD_ID, "bound_scrolls"));
    public static final TagKey<Item> RETURN_SCROLLS = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Waystones.MOD_ID, "return_scrolls"));
    public static final TagKey<Item> WARP_SCROLLS = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Waystones.MOD_ID, "warp_scrolls"));
    public static final TagKey<Item> WARP_STONES = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Waystones.MOD_ID, "warp_stones"));

    public static final TagKey<Block> IS_TELEPORT_TARGET = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(Waystones.MOD_ID, "is_teleport_target"));
    public static final TagKey<Block> WAYSTONES = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(Waystones.MOD_ID, "waystones"));
    public static final TagKey<Block> SHARESTONES = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(Waystones.MOD_ID, "sharestones"));

}
