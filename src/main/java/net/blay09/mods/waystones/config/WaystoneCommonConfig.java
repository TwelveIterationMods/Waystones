package net.blay09.mods.waystones.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Collections;
import java.util.List;

public class WaystoneCommonConfig {
    public final ForgeConfigSpec.BooleanValue addVillageStructure;
    public final ForgeConfigSpec.IntValue worldGenFrequency;
    public final ForgeConfigSpec.ConfigValue<List<String>> customWaystoneNames;

    WaystoneCommonConfig(ForgeConfigSpec.Builder builder) {
        builder.comment("The new config sync is great, but common configs don't sync to clients. Therefore all synced options need to be in the server config, which is in your world directory. Hopefully at some point Forge will sync COMMON too so we don't need to split options so much.").push("common");

        builder.push("worldgen");

        addVillageStructure = builder
                .comment("Set to true if waystones should be added to the generation of villages.")
                .translation("config.waystones.addVillageStructure")
                .define("addVillageStructure", false);

        worldGenFrequency = builder
                .comment("Approximate chunk distance between waystones generated freely in world generation")
                .translation("config.waystones.worldGenFrequency")
                .defineInRange("worldGenFrequency", 30, 0, Integer.MAX_VALUE);

        customWaystoneNames = builder
                .comment("The Name Generator will pick from these names until they have all been used, then it will generate random ones again.")
                .translation("config.waystones.customWaystoneNames")
                .define("customNames", Collections.emptyList());
    }
}
