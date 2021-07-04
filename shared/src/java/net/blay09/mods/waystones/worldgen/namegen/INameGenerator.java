package net.blay09.mods.waystones.worldgen.namegen;

import org.jetbrains.annotations.Nullable;

import java.util.Random;

public interface INameGenerator {
    @Nullable
    String randomName(Random rand);
}