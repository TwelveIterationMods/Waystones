package net.blay09.mods.waystones;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber(modid = Waystones.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class WaystoneConfig {

    public static class Common {
        public final ForgeConfigSpec.BooleanValue waystoneXpCost;
        public final ForgeConfigSpec.BooleanValue warpStoneXpCost;
        public final ForgeConfigSpec.BooleanValue globalNoCooldown;
        public final ForgeConfigSpec.BooleanValue globalInterDimension;
        public final ForgeConfigSpec.BooleanValue resetUseOnDamage;
        public final ForgeConfigSpec.BooleanValue disallowBreakingGenerated;
        public final ForgeConfigSpec.BooleanValue addVillageStructure;
        public final ForgeConfigSpec.BooleanValue createJourneyMapWaypoint;
        public final ForgeConfigSpec.IntValue worldGenFrequency;
        public final ForgeConfigSpec.ConfigValue<List<String>> customNames;
        public final ForgeConfigSpec.ConfigValue<String> teleportButtonTarget;
        public final ForgeConfigSpec.BooleanValue inventoryButtonXpCost;

        Common(ForgeConfigSpec.Builder builder) {
            builder.comment("Waystone Configuration").push("common");

            teleportButtonTarget = builder
                    .comment("Set this to a global waystone name to lock the inventory button to that specific waystone. Leave empty for default behaviour.")
                    .translation("waystones.config.teleportButtonTarget")
                    .define("teleportButtonTarget", "");

            waystoneXpCost = builder
                    .comment("If enabled, waystones cost experience when used, based on the distance travelled.")
                    .translation("waystones.config.waystoneXpCost")
                    .define("waystoneXpCost", true);

            inventoryButtonXpCost = builder
                    .comment("If enabled, the inventory button cost experience when used, based on the distance travelled.")
                    .translation("waystones.config.inventoryButtonXpCost")
                    .define("inventoryButtonXpCost", false);

            warpStoneXpCost = builder
                    .comment("If enabled, the warp stone costs experience when used, based on the distance travelled.")
                    .translation("waystones.config.warpStoneXpCost")
                    .define("warpStoneXpCost", false);

            globalNoCooldown = builder
                    .comment("If enabled, waystones marked as global have no cooldown.")
                    .translation("waystones.config.globalNoCooldown")
                    .define("globalNoCooldown", true);

            globalInterDimension = builder
                    .comment("If enabled, waystones marked as global work inter-dimensionally.")
                    .translation("waystones.config.globalInterDimension")
                    .define("globalInterDimension", true);

            resetUseOnDamage = builder
                    .comment("Whether the use of a warp stone or warp scroll should be interrupted by damage. Not supported when playing with Vivecraft!")
                    .translation("waystones.config.resetUseOnDamage")
                    .define("resetUseOnDamage", false);

            disallowBreakingGenerated = builder
                    .comment("Whether generated waystones should not be breakable by players.")
                    .translation("waystones.config.disallowBreakingGenerated")
                    .define("disallowBreakingGenerated", false);

            createJourneyMapWaypoint = builder
                    .comment("If this is true, activating a waystone will cause a JourneyMap waypoint to be created at its position.")
                    .translation("waystones.config.createJourneyMapWaypoint")
                    .define("createJourneyMapWaypoint", false);

            addVillageStructure = builder
                    .comment("Set to true if waystones should be added to the generation of villages.")
                    .translation("waystones.config.addVillageStructure")
                    .define("addVillageStructure", false);

            worldGenFrequency = builder
                    .comment("Approximate chunk distance between waystones generated freely in world generation")
                    .translation("waystones.config.worldGenFrequency")
                    .defineInRange("worldGenFrequency", 30, 0, Integer.MAX_VALUE);

            customNames = builder
                    .comment("The Name Generator will pick from these names until they have all been used, then it will generate random ones again.")
                    .translation("waystones.config.customNames")
                    .define("customNames", Collections.emptyList());
        }
    }


    public static class Server {
        public final ForgeConfigSpec.BooleanValue teleportButton;
        public final ForgeConfigSpec.ConfigValue<Integer> teleportButtonCooldown;
        public final ForgeConfigSpec.BooleanValue teleportButtonReturnOnly;
        public final ForgeConfigSpec.ConfigValue<Integer> warpStoneCooldown;
        public final ForgeConfigSpec.BooleanValue interDimension;
        public final ForgeConfigSpec.BooleanValue creativeModeOnly;
        public final ForgeConfigSpec.BooleanValue setSpawnPoint;
        public final ForgeConfigSpec.BooleanValue restrictRenameToOwner;
        public final ForgeConfigSpec.ConfigValue<Integer> blocksPerXPLevel;
        public final ForgeConfigSpec.IntValue maximumXpCost;
        public final ForgeConfigSpec.BooleanValue allowEveryoneGlobal;
        public final ForgeConfigSpec.IntValue warpStoneUseTime;
        public final ForgeConfigSpec.IntValue warpScrollUseTime;
        public final ForgeConfigSpec.BooleanValue globalWaystonesCostXp;

        Server(ForgeConfigSpec.Builder builder) {
            builder.comment("Waystones Configuration").push("server");

            teleportButton = builder
                    .comment("Should there be a button in the inventory to access the waystone menu?")
                    .translation("waystones.config.teleportButton")
                    .define("teleportButton", false);

            teleportButtonCooldown = builder
                    .comment("The cooldown between usages of the teleport button in seconds.")
                    .translation("waystones.config.teleportButtonCooldown")
                    .define("teleportButtonCooldown", 300);

            teleportButtonReturnOnly = builder
                    .comment("If enabled, the teleport button will only let you return to the last activated waystone, instead of allowing to choose.")
                    .translation("waystones.config.teleportButtonReturnOnly")
                    .define("teleportButtonReturnOnly", true);

            blocksPerXPLevel = builder
                    .comment("The amount of blocks per xp level requirement.")
                    .translation("waystones.config.blocksPerXPLevel")
                    .define("blocksPerXPLevel", 500);

            globalWaystonesCostXp = builder
                    .comment("Set to false to make all global waystones not cost any experience, regardless of method used.")
                    .translation("waystones.config.globalWaystonesCostXp")
                    .define("globalWaystonesCostXp", true);

            warpStoneCooldown = builder
                    .comment("The cooldown between usages of the warp stone in seconds.")
                    .translation("waystones.config.warpStoneCooldown")
                    .define("warpStoneCooldown", 300);

            interDimension = builder
                    .comment("If enabled, all waystones work inter-dimensionally.")
                    .translation("waystones.config.interDimension")
                    .define("interDimension", true);

            restrictRenameToOwner = builder
                    .comment("If enabled, only the owner of a waystone can rename it.")
                    .translation("waystones.config.restrictRenameToOwner")
                    .define("restrictRenameToOwner", false);

            setSpawnPoint = builder
                    .comment("If enabled, the player's spawnpoint will be set to the last activated waystone.")
                    .translation("waystones.config.setSpawnPoint").define("setSpawnPoint", false);

            allowEveryoneGlobal = builder
                    .comment("If enabled, everyone can create global waystones, not just players in creative mode.")
                    .translation("waystones.config.allowEveryoneGlobal")
                    .define("allowEveryoneGlobal", false);

            creativeModeOnly = builder
                    .comment("If enabled, waystones can only be placed in creative mode.")
                    .translation("waystones.config.creativeModeOnly")
                    .define("creativeModeOnly", false);

            maximumXpCost = builder
                    .comment("The maximum xp cost when Blocks per XP Level is enabled.")
                    .translation("waystones.config.maximumXpCost")
                    .defineInRange("maximumXpCost", 3, 1, Integer.MAX_VALUE);

            warpStoneUseTime = builder
                    .comment("The time it takes to use a warp stone in ticks. This is the charge-up time when holding right-click.")
                    .translation("waystones.config.warpStoneUseTime")
                    .defineInRange("warpStoneUseTime", 32, 1, 127);

            warpScrollUseTime = builder
                    .comment("The time it takes to use a warp scroll in ticks. This is the charge-up time when holding right-click.")
                    .translation("waystones.config.warpScrollUseTime")
                    .defineInRange("warpScrollUseTime", 32, 1, 127);
        }
    }

    public static class Client {
        public final ForgeConfigSpec.ConfigValue<Integer> teleportButtonX;
        public final ForgeConfigSpec.ConfigValue<Integer> teleportButtonY;
        public final ForgeConfigSpec.BooleanValue disableParticles;
        public final ForgeConfigSpec.BooleanValue disableTextGlow;
        public final ForgeConfigSpec.BooleanValue randomlySpawnedLookMossy;
        public final ForgeConfigSpec.DoubleValue soundVolume;

        Client(ForgeConfigSpec.Builder builder) {
            builder.comment("Waystones Configuration").push("client");

            teleportButtonX = builder
                    .comment("The x position of the warp button in the inventory.")
                    .translation("waystones.config.teleportButtonX")
                    .define("teleportButtonX", 58);

            teleportButtonY = builder
                    .comment("The y position of the warp button in the inventory.")
                    .translation("waystones.config.teleportButtonY")
                    .define("teleportButtonY", 60);

            disableParticles = builder
                    .comment("If enabled, activated waystones will not emit particles.")
                    .translation("waystones.config.disableParticles")
                    .define("disableParticles", false);

            disableTextGlow = builder
                    .comment("If enabled, the text overlay on waystones will no longer always render at full brightness.")
                    .translation("waystones.config.disableTextGlow")
                    .define("disableTextGlow", false);

            randomlySpawnedLookMossy = builder
                    .comment("If enabled, waystones spawned using the legacy spawn-just-anywhere mode will look mossy ingame.")
                    .translation("waystones.config.randomlySpawnedLookMossy")
                    .define("randomlySpawnedLookMossy", true);

            soundVolume = builder
                    .comment("The volume of the sound played when teleporting.")
                    .translation("waystones.config.soundVolume")
                    .defineInRange("soundVolume", 0.5f, 0f, 1f);
        }
    }

    static final ForgeConfigSpec commonSpec;
    public static final Common COMMON;

    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        commonSpec = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    static final ForgeConfigSpec serverSpec;
    public static final Server SERVER;

    static {
        final Pair<Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Server::new);
        serverSpec = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    static final ForgeConfigSpec clientSpec;
    public static final Client CLIENT;

    static {
        final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
        clientSpec = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

}
