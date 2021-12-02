package net.blay09.mods.waystones.worldgen.namegen;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MixedNameGenerator implements INameGenerator {

    private final List<INameGenerator> nameGenerators;

    public MixedNameGenerator(INameGenerator... nameGenerators) {
        this.nameGenerators = Arrays.asList(nameGenerators);
    }

    @Override
    public String randomName(Random rand) {
        Collections.shuffle(nameGenerators);
        for (INameGenerator nameGenerator : nameGenerators) {
            String name = nameGenerator.randomName(rand);
            if (name != null) {
                return name;
            }
        }

        return null;
    }
}
