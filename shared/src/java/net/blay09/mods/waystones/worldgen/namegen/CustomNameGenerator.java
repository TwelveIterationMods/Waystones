package net.blay09.mods.waystones.worldgen.namegen;

import net.blay09.mods.waystones.config.WaystonesConfig;

import java.util.*;

public class CustomNameGenerator implements INameGenerator {

    private final boolean allowDuplicates;
    private final Set<String> usedNames;

    public CustomNameGenerator(boolean allowDuplicates, Set<String> usedNames) {
        this.allowDuplicates = allowDuplicates;
        this.usedNames = usedNames;
    }

    @Override
    public String randomName(Random rand) {
        List<String> customNames = WaystonesConfig.getActive().customWaystoneNames();
        Collections.shuffle(customNames, rand);
        for (String customName : customNames) {
            if (allowDuplicates || !usedNames.contains(customName)) {
                return customName;
            }
        }

        return null;
    }
}
