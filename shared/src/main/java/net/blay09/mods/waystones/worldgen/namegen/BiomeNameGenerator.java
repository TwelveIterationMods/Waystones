package net.blay09.mods.waystones.worldgen.namegen;

import net.blay09.mods.waystones.api.IWaystone;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;

public class BiomeNameGenerator implements INameGenerator {
    @Override
    public String generateName(LevelAccessor level, IWaystone waystone, RandomSource rand) {
        final var biome = level.getBiome(waystone.getPos());
        return biome.unwrapKey()
                .map(ResourceKey::location)
                .map(ResourceLocation::toString)
                .map(it -> "biome." + it.replace(":", "."))
                .map(Component::translatable)
                .map(Component::getString)
                .orElse("Corrupted Lands");
    }
}
