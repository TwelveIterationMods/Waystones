package net.blay09.mods.waystones.worldgen.namegen;

import net.blay09.mods.waystones.api.Waystone;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;

import java.util.HashMap;
import java.util.Map;

public class TemplateNameGenerator implements INameGenerator {
    private final String template;
    private final Map<String, INameGenerator> placeholders = new HashMap<>();

    public TemplateNameGenerator(String template) {
        this.template = template;
    }

    public TemplateNameGenerator with(String key, INameGenerator nameGenerator) {
        placeholders.put(key, nameGenerator);
        return this;
    }

    public TemplateNameGenerator with(String key, String value) {
        return with(key, new LiteralNameGenerator(value));
    }

    @Override
    public String generateName(LevelAccessor level, Waystone waystone, RandomSource rand) {
        var result = template;
        for (final var entry : placeholders.entrySet()) {
            final var key = entry.getKey();
            final var value = entry.getValue().generateName(level, waystone, rand);
            if (value != null) {
                result = result.replaceAll("\\{" + key + "}", value);
            }
        }
        return result;
    }
}
