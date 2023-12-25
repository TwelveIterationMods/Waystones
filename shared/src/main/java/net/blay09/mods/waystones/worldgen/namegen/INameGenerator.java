package net.blay09.mods.waystones.worldgen.namegen;

import net.blay09.mods.waystones.api.Waystone;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.Nullable;

public interface INameGenerator {
    @Nullable
    String generateName(LevelAccessor level, Waystone waystone, RandomSource rand);
}