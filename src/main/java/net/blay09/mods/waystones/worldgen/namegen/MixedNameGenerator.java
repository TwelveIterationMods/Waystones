package net.blay09.mods.waystones.worldgen.namegen;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Random;

public class MixedNameGenerator implements INameGenerator {

    private final INameGenerator[] nameGenerators;

    public MixedNameGenerator(INameGenerator... nameGenerators) {
        this.nameGenerators = nameGenerators;
    }

    @Override
    public String randomName(Random rand) {
        ArrayUtils.shuffle(nameGenerators);
        for (INameGenerator nameGenerator : nameGenerators) {
            String name = nameGenerator.randomName(rand);
            if (name != null) {
                return name;
            }
        }

        return null;
    }
}
