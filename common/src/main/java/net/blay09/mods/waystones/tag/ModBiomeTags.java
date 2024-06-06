package net.blay09.mods.waystones.tag;

import net.blay09.mods.waystones.Waystones;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public class ModBiomeTags {
    public static final TagKey<Biome> IS_DESERT = TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(Waystones.MOD_ID, "is_desert"));
    public static final TagKey<Biome> IS_SWAMP = TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(Waystones.MOD_ID, "is_swamp"));
    public static final TagKey<Biome> IS_MUSHROOM = TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(Waystones.MOD_ID, "is_mushroom"));
}
