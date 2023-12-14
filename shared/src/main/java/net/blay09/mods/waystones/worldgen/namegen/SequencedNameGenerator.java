package net.blay09.mods.waystones.worldgen.namegen;

import net.blay09.mods.waystones.api.IWaystone;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;

public class SequencedNameGenerator implements INameGenerator {

    private final INameGenerator[] nameGenerators;

    public SequencedNameGenerator(INameGenerator... nameGenerators) {
        this.nameGenerators = nameGenerators;
    }

    @Override
    public String generateName(LevelAccessor level, IWaystone waystone, RandomSource rand) {
        for (INameGenerator nameGenerator : nameGenerators) {
            String name = nameGenerator.generateName(level, waystone, rand);
            if (name != null) {
                return name;
            }
        }
        return null;
    }
}
