package net.blay09.mods.waystones.worldgen.namegen;

import net.blay09.mods.waystones.config.WaystonesConfig;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class CustomNameGenerator implements INameGenerator {

    private final boolean allowDuplicates;
    private final Set<String> usedNames;

    public CustomNameGenerator(boolean allowDuplicates, Set<String> usedNames) {
        this.allowDuplicates = allowDuplicates;
        this.usedNames = usedNames;
    }

    @Override
    public String randomName(Random rand) {
        List<? extends String> customNames = WaystonesConfig.COMMON.customWaystoneNames.get();
        Collections.shuffle(customNames, rand);
        for (String customName : customNames) {
            if (allowDuplicates || !usedNames.contains(customName)) {
                return customName;
            }
        }

        return null;
    }
}
