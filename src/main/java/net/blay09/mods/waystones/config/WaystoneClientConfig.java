package net.blay09.mods.waystones.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class WaystoneClientConfig {

    public final ForgeConfigSpec.ConfigValue<Integer> teleportButtonX;
    public final ForgeConfigSpec.ConfigValue<Integer> teleportButtonY;
    public final ForgeConfigSpec.ConfigValue<Integer> creativeTeleportButtonX;
    public final ForgeConfigSpec.ConfigValue<Integer> creativeTeleportButtonY;
    public final ForgeConfigSpec.BooleanValue disableParticles;
    public final ForgeConfigSpec.BooleanValue disableTextGlow;
    public final ForgeConfigSpec.BooleanValue displayWaystonesOnJourneyMap;
    public final ForgeConfigSpec.DoubleValue soundVolume;

    WaystoneClientConfig(ForgeConfigSpec.Builder builder) {
        builder.push("client");

        teleportButtonX = builder
                .comment("The x position of the warp button in the inventory.")
                .translation("config.waystones.teleportButtonX")
                .define("teleportButtonX", 58);

        teleportButtonY = builder
                .comment("The y position of the warp button in the inventory.")
                .translation("config.waystones.teleportButtonY")
                .define("teleportButtonY", 60);

        creativeTeleportButtonX = builder
                .comment("The x position of the warp button in the creative menu.")
                .translation("config.waystones.creativeTeleportButtonX")
                .define("creativeTeleportButtonX", 58);

        creativeTeleportButtonY = builder
                .comment("The y position of the warp button in the creative menu.")
                .translation("config.waystones.creativeTeleportButtonY")
                .define("creativeTeleportButtonY", 60);

        disableParticles = builder
                .comment("If enabled, activated waystones will not emit particles.")
                .translation("config.waystones.disableParticles")
                .define("disableParticles", false);

        disableTextGlow = builder
                .comment("If enabled, the text overlay on waystones will no longer always render at full brightness.")
                .translation("config.waystones.disableTextGlow")
                .define("disableTextGlow", false);

        soundVolume = builder
                .comment("The volume of the sound played when teleporting.")
                .translation("config.waystones.soundVolume")
                .defineInRange("soundVolume", 0.1f, 0f, 1f);

        displayWaystonesOnJourneyMap = builder
                .comment("If enabled, JourneyMap waypoints will be created for each activated waystone.")
                .translation("config.waystones.displayWaystonesOnJourneyMap")
                .define("displayWaystonesOnJourneyMap", false);
    }
}
