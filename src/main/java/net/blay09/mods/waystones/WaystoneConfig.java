package net.blay09.mods.waystones;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.common.config.Config;

@Config(modid = Waystones.MOD_ID, type = Config.Type.INSTANCE, name = "Waystones", category = "")
@Config.LangKey("waystones.config")
public class WaystoneConfig {

    public static General general = new General();
    public static WorldGen worldGen = new WorldGen();
    public static Compat compat = new Compat();
    public static Client client = new Client();

    public static class General {

        @Config.Name("Teleport Button in GUI")
        @Config.Comment("Should there be a button in the inventory to access the waystone menu?")
        public boolean teleportButton = false;

        @Config.Name("Teleport Button Target")
        @Config.Comment("Set this to a global waystone name to lock the inventory button to that specific waystone. Leave empty for default behaviour.")
        public String teleportButtonTarget = "";

        @Config.Name("Teleport Button Cooldown")
        @Config.Comment("The cooldown between usages of the teleport button in seconds.")
        public int teleportButtonCooldown = 300;

        @Config.Name("Teleport Button Return Only")
        @Config.Comment("If enabled, the teleport button will only let you return to the last activated waystone, instead of allowing to choose.")
        public boolean teleportButtonReturnOnly = true;

        @Config.Name("Blocks per XP Level")
        @Config.Comment("The amount of blocks per xp level requirement.")
        public int blocksPerXPLevel = 500;

        @Config.Name("Short Travel is Free")
        @Config.Comment("If set to true, XP cost by distance travelled will only start beyond the distance-per-level distance, and shorter travel will be free.")
        public boolean shortTravelFree = true;

        @Config.Name("Maximum XP Cost")
        @Config.Comment("The maximum xp cost when Blocks per XP Level is enabled.")
        @Config.RangeInt(min = 1)
        public int maximumXpCost = 3;

        @Config.Name("Waystone Costs XP")
        @Config.Comment("If enabled, waystones cost experience when used, based on the distance travelled.")
        public boolean waystoneXpCost = true;

        @Config.Name("Inventory Button Costs XP")
        @Config.Comment("If enabled, the inventory button cost experience when used, based on the distance travelled.")
        public boolean inventoryButtonXpCost = false;

        @Config.Name("Warp Stone Costs XP")
        @Config.Comment("If enabled, the warp stone costs experience when used, based on the distance travelled.")
        public boolean warpStoneXpCost = false;

        @Config.Name("Global Waystones Cost XP")
        @Config.Comment("Set to false to make all global waystones not cost any experience, regardless of method used.")
        public boolean globalWaystonesCostXp = true;

        @Config.Name("Warp Stone Cooldown")
        @Config.Comment("The cooldown between usages of the warp stone in seconds.")
        public int warpStoneCooldown = 300;

        @Config.Name("Interdimensional Teleport")
        @Config.Comment("If enabled, all waystones work inter-dimensionally.")
        public boolean interDimension = true;

        @Config.Name("Restrict Rename to Owner")
        @Config.Comment("If enabled, only the owner of a waystone can rename it.")
        public boolean restrictRenameToOwner = false;

        @Config.Name("Creative Mode Only")
        @Config.Comment("If enabled, waystones can only be placed in creative mode.")
        public boolean creativeModeOnly;

        @Config.Name("Set Spawnpoint on Activation")
        @Config.Comment("If enabled, the player's spawnpoint will be set to the last activated waystone.")
        public boolean setSpawnPoint = false;

        @Config.Name("No Cooldown on Global Waystones")
        @Config.Comment("If enabled, waystones marked as global have no cooldown.")
        public boolean globalNoCooldown = true;

        @Config.Name("Interdimensional Teleport on Global Waystones")
        @Config.Comment("If enabled, waystones marked as global work inter-dimensionally.")
        public boolean globalInterDimension = true;

        @Config.Name("Allow Global Waystones for Everyone")
        @Config.Comment("If enabled, everyone can create global waystones, not just players in creative mode.")
        public boolean allowEveryoneGlobal = false;

        @Config.Name("Reset Use On Damage")
        @Config.Comment("Whether the use of a warp stone or warp scroll should be interrupted by damage. Not supported when playing with Vivecraft!")
        public boolean resetUseOnDamage = false;

        @Config.Name("Disallow Breaking Generated")
        @Config.Comment("Whether generated waystones should not be breakable by players.")
        public boolean disallowBreakingGenerated = false;

        @Config.Name("Warp Stone Use Time")
        @Config.Comment("The time it takes to use a warp stone in ticks. This is the charge-up time when holding right-click.")
        @Config.RangeInt(min = 1, max = 127)
        public int warpStoneUseTime = 32;

        @Config.Name("Warp Scroll Use Time")
        @Config.Comment("The time it takes to use a warp scroll in ticks. This is the charge-up time when holding right-click.")
        @Config.RangeInt(min = 1, max = 127)
        public int warpScrollUseTime = 32;
    }

    public static class WorldGen {
        @Config.Name("Generate in Villages")
        @Config.Comment("The chance for a waystone to generate in a village. Set to 1 to always generate one in villages, set to 0 to disable.")
        @Config.RangeDouble(min = 0, max = 1)
        public float villageChance = 1f;

        @Config.Name("Change to generate just anywhere")
        @Config.Comment("The chance for a waystone to generate just anywhere (without a structure), scaled by 1/10000.")
        @Config.RangeDouble(min = 0, max = 10000)
        public float legacyChance = 0f;

        @Config.Name("Custom Names")
        @Config.Comment("The Name Generator will pick from these names until they have all been used, then it will generate random ones again.")
        public String[] customNames = new String[0];
    }

    public static class Compat {
        @Config.Name("Create JourneyMap Waypoint")
        @Config.Comment("If this is true, activating a waystone will cause a JourneyMap waypoint to be created at its position.")
        public boolean createJourneyMapWaypoint = false;
    }

    public static class Client {
        @Config.Name("Sound Volume")
        @Config.Comment("The volume of the sound played when teleporting.")
        @Config.RangeDouble(min = 0f, max = 1f)
        public float soundVolume = 0.5f;

        @Config.Name("Teleport Button GUI X")
        @Config.Comment("The x position of the warp button in the inventory.")
        public int teleportButtonX = 58;

        @Config.Name("Teleport Button GUI Y")
        @Config.Comment("The y position of the warp button in the inventory.")
        public int teleportButtonY = 60;

        @Config.Name("Disable Particles")
        @Config.Comment("If enabled, activated waystones will not emit particles.")
        public boolean disableParticles = false;

        @Config.Name("Disable Text Glow")
        @Config.Comment("If enabled, the text overlay on waystones will no longer always render at full brightness.")
        public boolean disableTextGlow = false;

        @Config.Name("Mossy Look for World Gen Waystones")
        @Config.Comment("If enabled, waystones spawned using the legacy spawn-just-anywhere mode will look mossy ingame.")
        public boolean randomlySpawnedLookMossy = true;
    }

    public static void read(ByteBuf buf) {
        general.teleportButton = buf.readBoolean();
        general.teleportButtonCooldown = buf.readInt();
        general.teleportButtonReturnOnly = buf.readBoolean();
        general.warpStoneCooldown = buf.readInt();
        general.interDimension = buf.readBoolean();
        general.creativeModeOnly = buf.readBoolean();
        general.setSpawnPoint = buf.readBoolean();
        general.restrictRenameToOwner = buf.readBoolean();
        general.blocksPerXPLevel = buf.readInt();
        general.maximumXpCost = buf.readInt();
        general.allowEveryoneGlobal = buf.readBoolean();
        general.warpStoneUseTime = buf.readByte();
        general.warpScrollUseTime = buf.readByte();
        general.globalWaystonesCostXp = buf.readBoolean();
        general.warpStoneXpCost = buf.readBoolean();
        general.shortTravelFree = buf.readBoolean();
    }

    public static void write(ByteBuf buf) {
        buf.writeBoolean(general.teleportButton);
        buf.writeInt(general.teleportButtonCooldown);
        buf.writeBoolean(general.teleportButtonReturnOnly);
        buf.writeInt(general.warpStoneCooldown);
        buf.writeBoolean(general.interDimension);
        buf.writeBoolean(general.creativeModeOnly);
        buf.writeBoolean(general.setSpawnPoint);
        buf.writeBoolean(general.restrictRenameToOwner);
        buf.writeInt(general.blocksPerXPLevel);
        buf.writeInt(general.maximumXpCost);
        buf.writeBoolean(general.allowEveryoneGlobal);
        buf.writeByte(general.warpStoneUseTime);
        buf.writeByte(general.warpScrollUseTime);
        buf.writeBoolean(general.globalWaystonesCostXp);
        buf.writeBoolean(general.warpStoneXpCost);
        buf.writeBoolean(general.shortTravelFree);
    }
}
