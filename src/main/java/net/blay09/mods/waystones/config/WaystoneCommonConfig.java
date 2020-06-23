package net.blay09.mods.waystones.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class WaystoneCommonConfig {
    public final ForgeConfigSpec.BooleanValue addVillageStructure;
    public final ForgeConfigSpec.IntValue worldGenFrequency;
    public final ForgeConfigSpec.EnumValue<WorldGenStyle> worldGenStyle;
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> customWaystoneNames;

    WaystoneCommonConfig(ForgeConfigSpec.Builder builder) {
        builder.comment(WaystoneConfig.CONFIGS_NOTE).push("common");

        builder.push("worldgen");

        addVillageStructure = builder
                .comment("Set to true if waystones should be added to the generation of villages.")
                .translation("config.waystones.addVillageStructure")
                .define("addVillageStructure", false);

        worldGenFrequency = builder
                .comment("Approximate chunk distance between waystones generated freely in world generation. Set to 0 to disable generation.")
                .translation("config.waystones.worldGenFrequency")
                .defineInRange("worldGenFrequency", 30, 0, Integer.MAX_VALUE);

        worldGenStyle = builder
                .comment("Set to 'DEFAULT' to only generate the normally textured waystones. Set to 'MOSSY' or 'SANDY' to generate all as that variant. Set to 'BIOME' to make the style depend on the biome it is generated in.")
                .translation("config.waystones.worldGenStyle")
                .defineEnum("worldGenStyle", WorldGenStyle.BIOME);

        customWaystoneNames = builder
                .comment("The Name Generator will pick from these names until they have all been used, then it will generate random ones again.")
                .translation("config.waystones.customWaystoneNames")
                .defineList("customWaystoneNames", ArrayList::new, it -> it instanceof String);
    }
}
