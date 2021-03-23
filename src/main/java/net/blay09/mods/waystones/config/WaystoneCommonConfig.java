package net.blay09.mods.waystones.config;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class WaystoneCommonConfig {
    public final ForgeConfigSpec.BooleanValue addVillageStructure;
    public final ForgeConfigSpec.BooleanValue allowWaystoneToWaystoneTeleport;
    public final ForgeConfigSpec.IntValue worldGenFrequency;
    public final ForgeConfigSpec.EnumValue<WorldGenStyle> worldGenStyle;
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> worldGenDimensionAllowList;
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> worldGenDimensionDenyList;
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> dimensionalWarpAllowList;
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> dimensionalWarpDenyList;
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> customWaystoneNames;

    WaystoneCommonConfig(ForgeConfigSpec.Builder builder) {
        builder.push("common");

        builder.push("worldgen");

        addVillageStructure = builder
                .comment("Set to true if waystones should be added to the generation of villages.")
                .translation("config.waystones.addVillageStructure")
                .define("addVillageStructure", true);

        allowWaystoneToWaystoneTeleport = builder
                .comment("Set to true if players should be able to teleport between waystones by simply right-clicking a waystone.")
                .translation("config.waystones.allowWaystoneToWaystoneTeleport")
                .define("allowWaystoneToWaystoneTeleport", true);

        worldGenFrequency = builder
                .comment("Approximate chunk distance between waystones generated freely in world generation. Set to 0 to disable generation.")
                .translation("config.waystones.worldGenFrequency")
                .defineInRange("worldGenFrequency", 30, 0, Integer.MAX_VALUE);

        worldGenStyle = builder
                .comment("Set to 'DEFAULT' to only generate the normally textured waystones. Set to 'MOSSY' or 'SANDY' to generate all as that variant. Set to 'BIOME' to make the style depend on the biome it is generated in.")
                .translation("config.waystones.worldGenStyle")
                .defineEnum("worldGenStyle", WorldGenStyle.BIOME);

        worldGenDimensionAllowList = builder
                .comment("List of dimensions that waystones are allowed to spawn in through world gen. If left empty, all dimensions except those in worldGenDimensionDenyList are used.")
                .translation("config.waystones.worldGenDimensionAllowList")
                .defineList("worldGenDimensionAllowList", Lists.newArrayList("minecraft:overworld", "minecraft:the_nether", "minecraft:the_end"), it -> it instanceof String);

        worldGenDimensionDenyList = builder
                .comment("List of dimensions that waystones are not allowed to spawn in through world gen. Only used if worldGenDimensionAllowList is empty.")
                .translation("config.waystones.worldGenDimensionDenyList")
                .defineList("worldGenDimensionDenyList", ArrayList::new, it -> it instanceof String);

        dimensionalWarpAllowList = builder
                .comment("List of dimensions that players are allowed to warp cross-dimension from and to. If left empty, all dimensions except those in dimensionalWarpDenyList are allowed.")
                .translation("config.waystones.dimensionalWarpAllowList")
                .defineList("dimensionalWarpAllowList", ArrayList::new, it -> it instanceof String);

        dimensionalWarpDenyList = builder
                .comment("List of dimensions that players are not allowed to warp cross-dimension from and to. Only used if dimensionalWarpAllowList is empty.")
                .translation("config.waystones.worldGenDimensionDenyList")
                .defineList("worldGenDimensionDenyList", ArrayList::new, it -> it instanceof String);

        customWaystoneNames = builder
                .comment("The Name Generator will pick from these names until they have all been used, then it will generate random ones again.")
                .translation("config.waystones.customWaystoneNames")
                .defineList("customWaystoneNames", ArrayList::new, it -> it instanceof String);
    }
}
