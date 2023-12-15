package net.blay09.mods.waystones.worldgen.namegen;

import net.blay09.mods.waystones.api.IWaystone;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;

public class LiteralNameGenerator implements INameGenerator {

    private final String literal;

    public LiteralNameGenerator(String literal) {
        this.literal = literal;
    }

    @Override
    public String generateName(LevelAccessor level, IWaystone waystone, RandomSource rand) {
        return literal;
    }
}
