package net.blay09.mods.waystones.worldgen.namegen;

import net.blay09.mods.waystones.api.IWaystone;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

public interface INameGenerator {
    @Nullable
    String generateName(LevelAccessor level, IWaystone waystone, RandomSource rand);
}