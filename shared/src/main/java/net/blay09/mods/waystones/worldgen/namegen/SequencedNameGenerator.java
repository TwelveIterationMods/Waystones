package net.blay09.mods.waystones.worldgen.namegen;

import java.util.Random;

public class SequencedNameGenerator implements INameGenerator {

    private final INameGenerator[] nameGenerators;

    public SequencedNameGenerator(INameGenerator... nameGenerators) {
        this.nameGenerators = nameGenerators;
    }

    @Override
    public String randomName(Random rand) {
        for (INameGenerator nameGenerator : nameGenerators) {
            String name = nameGenerator.randomName(rand);
            if (name != null) {
                return name;
            }
        }
        return null;
    }
}
