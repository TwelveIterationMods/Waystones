package net.blay09.mods.waystones.config;

import com.google.common.collect.Lists;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.blay09.mods.balm.api.config.BalmConfigData;
import net.blay09.mods.balm.api.config.Comment;
import net.blay09.mods.balm.api.config.ExpectedType;
import net.blay09.mods.balm.api.config.Synced;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.worldgen.namegen.NameGenerationMode;

import java.util.ArrayList;
import java.util.List;

@Config(name = Waystones.MOD_ID)
public class WaystonesConfigData implements BalmConfigData, IWaystonesConfig {
    @ConfigEntry.Gui.CollapsibleObject
    public XpCost xpCost = new XpCost();

    @ConfigEntry.Gui.CollapsibleObject
    public Restrictions restrictions = new Restrictions();

    @ConfigEntry.Gui.CollapsibleObject
    public Cooldowns cooldowns = new Cooldowns();

    @ConfigEntry.Gui.CollapsibleObject
    public InventoryButton inventoryButton = new InventoryButton();

    @ConfigEntry.Gui.CollapsibleObject
    public WorldGen worldGen = new WorldGen();

    @ConfigEntry.Gui.CollapsibleObject
    public Client client = new Client();

    @ConfigEntry.Gui.CollapsibleObject
    public Compatibility compatibility = new Compatibility();

    public static class XpCost {
        @Synced
        @Comment("Set to true if experience cost should be inverted, meaning the shorter the distance, the more expensive. Can be used to encourage other methods for short-distance travel.")
        public boolean inverseXpCost = false;

        @Synced
        @Comment("The amount of blocks per xp level requirement. If set to 500, the base xp cost for travelling 1000 blocks will be 2 levels.")
        public int blocksPerXpLevel = 1000;

        @Synced
        @Comment("The minimum base xp cost (may be subceeded by multipliers defined below)")
        public double minimumBaseXpCost = 0f;

        @Synced
        @Comment("The maximum base xp cost (may be exceeded by multipliers defined below), set to 0 to disable all distance-based XP costs")
        public double maximumBaseXpCost = 3f;

        @Synced
        @Comment("How much xp is needed per leashed animal to travel with you")
        public int xpCostPerLeashed = 1;

        @Synced
        @Comment("The base xp level cost when travelling between dimensions. Ignores block distance.")
        public int dimensionalWarpXpCost = 3;

        @Synced
        @Comment("The multiplier applied to the base xp cost when teleporting to a global waystone through any method.")
        public double globalWaystoneXpCostMultiplier = 1f;

        @Synced
        @Comment("The multiplier applied to the base xp cost when teleporting using a Warp Stone item (not the Waystone block, Konstantin)")
        public double warpStoneXpCostMultiplier = 0f;

        @Synced
        @Comment("The multiplier applied to the base xp cost when teleporting from one waystone to another.")
        public double waystoneXpCostMultiplier = 1f;

        @Synced
        @Comment("The multiplier applied to the base xp cost when teleporting from one sharestone to another.")
        public double sharestoneXpCostMultiplier = 1f;

        @Synced
        @Comment("The multiplier applied to the base xp cost when teleporting from a portstone.")
        public double portstoneXpCostMultiplier = 0f;

        @Synced
        @Comment("The multiplier applied to the base xp cost when teleporting from one warp plate to another.")
        public double warpPlateXpCostMultiplier = 0f;

        @Synced
        @Comment("The multiplier applied to the base xp cost when teleporting via the inventory button.")
        public double inventoryButtonXpCostMultiplier = 0f;
    }

    public static class Restrictions {
        @Synced
        @Comment("If enabled, only creative players can place, edit or break waystones. This does NOT disable the crafting recipe.")
        public boolean restrictToCreative = false;

        @Synced
        @Comment("If enabled, only the owner of a waystone (the one who placed it) can rename it.")
        public boolean restrictRenameToOwner = false;

        @Synced
        @Comment("If enabled, waystones generated in worldgen are unbreakable.")
        public boolean generatedWaystonesUnbreakable = false;

        @Synced
        @Comment("If enabled, leashed mobs will be teleported with you")
        public boolean transportLeashed = true;

        @Synced
        @Comment("Whether to take leashed mobs with you when teleporting between dimensions")
        public boolean transportLeashedDimensional = true;

        @Comment("List of leashed mobs that cannot be taken with you when teleporting")
        @ExpectedType(String.class)
        public List<String> leashedDenyList = Lists.newArrayList("minecraft:wither");

        @Synced
        @Comment("Set to 'ALLOW' to allow dimensional warp in general. Set to 'GLOBAL_ONLY' to restrict dimensional warp to global waystones. Set to 'DENY' to disallow all dimensional warps.")
        public DimensionalWarp dimensionalWarp = DimensionalWarp.ALLOW;

        @Comment("List of dimensions that players are allowed to warp cross-dimension from and to. If left empty, all dimensions except those in dimensionalWarpDenyList are allowed.")
        @ExpectedType(String.class)
        public List<String> dimensionalWarpAllowList = new ArrayList<>();

        @Comment("List of dimensions that players are not allowed to warp cross-dimension from and to. Only used if dimensionalWarpAllowList is empty.")
        @ExpectedType(String.class)
        public List<String> dimensionalWarpDenyList = new ArrayList<>();

        @Comment("Set to true if players should be able to teleport between waystones by simply right-clicking a waystone.")
        public boolean allowWaystoneToWaystoneTeleport = true;

        @Synced
        @Comment("Set to false to allow non-creative players to make waystones globally activated for all players.")
        public boolean globalWaystoneSetupRequiresCreativeMode = true;
    }

    public static class Cooldowns {
        @Synced
        @Comment("The multiplier applied to the cooldown when teleporting to a global waystone via inventory button or warp stone.")
        public double globalWaystoneCooldownMultiplier = 1f;

        @Synced
        @Comment("The cooldown between usages of the warp stone in seconds. This is bound to the player, not the item, so multiple warp stones share the same cooldown.")
        public int warpStoneCooldown = 300;

        @Synced
        @Comment("The time in ticks that it takes to use a warp stone. This is the charge-up time when holding right-click.")
        public int warpStoneUseTime = 32;

        @Synced
        @Comment("The time in ticks that it takes to use a warp plate. This is the time the player has to stand on top for.")
        public int warpPlateUseTime = 20;

        @Synced
        @Comment("The time in ticks it takes to use a scroll. This is the charge-up time when holding right-click.")
        public int scrollUseTime = 32;

        @Synced
        @Comment("The cooldown between usages of the inventory button in seconds.")
        public int inventoryButtonCooldown = 300;
    }

    public static class InventoryButton {
        @Synced
        @Comment("Set to 'NONE' for no inventory button. Set to 'NEAREST' for an inventory button that teleports to the nearest waystone. Set to 'ANY' for an inventory button that opens the waystone selection menu. Set to a waystone name for an inventory button that teleports to a specifically named waystone.")
        public String inventoryButton = "";

        @Comment("The x position of the warp button in the inventory.")
        public int warpButtonX = 58;

        @Comment("The y position of the warp button in the inventory.")
        public int warpButtonY = 60;

        @Comment("The y position of the warp button in the creative menu.")
        public int creativeWarpButtonX = 88;

        @Comment("The y position of the warp button in the creative menu.")
        public int creativeWarpButtonY = 33;
    }

    public static class WorldGen {
        @Comment("Set to 'DEFAULT' to only generate the normally textured waystones. Set to 'MOSSY' or 'SANDY' to generate all as that variant. Set to 'BIOME' to make the style depend on the biome it is generated in.")
        public WorldGenStyle worldGenStyle = WorldGenStyle.BIOME;

        @Comment("Approximate chunk distance between waystones generated freely in world generation. Set to 0 to disable generation.")
        public int frequency = 25;

        @Comment("List of dimensions that waystones are allowed to spawn in through world gen. If left empty, all dimensions except those in worldGenDimensionDenyList are used.")
        @ExpectedType(String.class)
        public List<String> dimensionAllowList = Lists.newArrayList("minecraft:overworld", "minecraft:the_nether", "minecraft:the_end");

        @Comment("List of dimensions that waystones are not allowed to spawn in through world gen. Only used if worldGenDimensionAllowList is empty.")
        @ExpectedType(String.class)
        public List<String> dimensionDenyList = new ArrayList<>();

        @Comment("Set to 'PRESET_FIRST' to first use names from the custom names list. Set to 'PRESET_ONLY' to use only those custom names. Set to 'MIXED' to have some waystones use custom names, and others random names.")
        public NameGenerationMode nameGenerationMode = NameGenerationMode.PRESET_FIRST;

        @Comment("These names will be used for the PRESET name generation mode. See the nameGenerationMode option for more info.")
        @ExpectedType(String.class)
        public List<String> customWaystoneNames = new ArrayList<>();

        @Comment("Set to true if waystones should be added to the generation of villages. Some villages may still spawn without a waystone.")
        public boolean spawnInVillages = true;

        @Comment("Ensures that pretty much every village will have a waystone, by spawning it as early as possible. In addition, this means waystones will generally be located in the center of the village.")
        public boolean forceSpawnInVillages = false;
    }

    public static class Compatibility {
        @Comment("If enabled, JourneyMap waypoints will be created for each activated waystone.")
        public boolean displayWaystonesOnJourneyMap = true;

        @Comment("If enabled, JourneyMap waypoints will only be created if the mod 'JourneyMap Integration' is not installed")
        public boolean preferJourneyMapIntegration = true;
    }

    public static class Client {
        @Comment("If enabled, the text overlay on waystones will no longer always render at full brightness.")
        public boolean disableTextGlow = false;

        @Comment("If enabled, activated waystones will not emit particles.")
        public boolean disableParticles = false;
    }

    @Override
    public boolean restrictToCreative() {
        return restrictions.restrictToCreative;
    }

    @Override
    public boolean generatedWaystonesUnbreakable() {
        return restrictions.generatedWaystonesUnbreakable;
    }

    @Override
    public boolean globalWaystoneRequiresCreative() {
        return restrictions.globalWaystoneSetupRequiresCreativeMode;
    }

    @Override
    public boolean restrictRenameToOwner() {
        return restrictions.restrictRenameToOwner;
    }

    @Override
    public boolean transportLeashed() {
        return restrictions.transportLeashed;
    }

    @Override
    public List<String> leashedDenyList() {
        return restrictions.leashedDenyList;
    }

    @Override
    public boolean transportLeashedDimensional() {
        return restrictions.transportLeashedDimensional;
    }

    @Override
    public DimensionalWarp dimensionalWarp() {
        return restrictions.dimensionalWarp;
    }

    @Override
    public List<String> dimensionalWarpAllowList() {
        return restrictions.dimensionalWarpAllowList;
    }

    @Override
    public List<String> dimensionalWarpDenyList() {
        return restrictions.dimensionalWarpDenyList;
    }

    @Override
    public boolean allowWaystoneToWaystoneTeleport() {
        return restrictions.allowWaystoneToWaystoneTeleport;
    }

    @Override
    public int inventoryButtonCooldown() {
        return cooldowns.inventoryButtonCooldown;
    }

    @Override
    public int warpStoneCooldown() {
        return cooldowns.warpStoneCooldown;
    }

    @Override
    public double globalWaystoneCooldownMultiplier() {
        return cooldowns.globalWaystoneCooldownMultiplier;
    }

    @Override
    public boolean inverseXpCost() {
        return xpCost.inverseXpCost;
    }

    @Override
    public int blocksPerXPLevel() {
        return xpCost.blocksPerXpLevel;
    }

    @Override
    public int dimensionalWarpXpCost() {
        return xpCost.dimensionalWarpXpCost;
    }

    @Override
    public double inventoryButtonXpCostMultiplier() {
        return xpCost.inventoryButtonXpCostMultiplier;
    }

    @Override
    public double warpStoneXpCostMultiplier() {
        return xpCost.warpStoneXpCostMultiplier;
    }

    @Override
    public double sharestoneXpCostMultiplier() {
        return xpCost.sharestoneXpCostMultiplier;
    }

    @Override
    public double waystoneXpCostMultiplier() {
        return xpCost.waystoneXpCostMultiplier;
    }

    @Override
    public double portstoneXpCostMultiplier() {
        return xpCost.portstoneXpCostMultiplier;
    }

    @Override
    public double warpPlateXpCostMultiplier() {
        return xpCost.warpPlateXpCostMultiplier;
    }

    @Override
    public double globalWaystoneXpCostMultiplier() {
        return xpCost.globalWaystoneXpCostMultiplier;
    }

    @Override
    public int xpCostPerLeashed() {
        return xpCost.xpCostPerLeashed;
    }

    @Override
    public int scrollUseTime() {
        return cooldowns.scrollUseTime;
    }

    @Override
    public int warpStoneUseTime() {
        return cooldowns.warpStoneUseTime;
    }

    @Override
    public List<String> customWaystoneNames() {
        return worldGen.customWaystoneNames;
    }

    @Override
    public boolean disableParticles() {
        return client.disableParticles;
    }

    @Override
    public double minimumXpCost() {
        return xpCost.minimumBaseXpCost;
    }

    @Override
    public double maximumXpCost() {
        return xpCost.maximumBaseXpCost;
    }

    @Override
    public String inventoryButton() {
        return inventoryButton.inventoryButton;
    }

    @Override
    public boolean disableTextGlow() {
        return client.disableTextGlow;
    }

    @Override
    public WorldGenStyle worldGenStyle() {
        return worldGen.worldGenStyle;
    }

    @Override
    public boolean spawnInVillages() {
        return worldGen.spawnInVillages;
    }

    @Override
    public boolean forceSpawnInVillages() {
        return worldGen.forceSpawnInVillages;
    }

    @Override
    public NameGenerationMode nameGenerationMode() {
        return worldGen.nameGenerationMode;
    }

    @Override
    public List<String> worldGenDimensionAllowList() {
        return worldGen.dimensionAllowList;
    }

    @Override
    public List<String> worldGenDimensionDenyList() {
        return worldGen.dimensionDenyList;
    }

    @Override
    public int worldGenFrequency() {
        return worldGen.frequency;
    }

    @Override
    public int creativeWarpButtonX() {
        return inventoryButton.creativeWarpButtonX;
    }

    @Override
    public int creativeWarpButtonY() {
        return inventoryButton.creativeWarpButtonY;
    }

    @Override
    public int warpButtonX() {
        return inventoryButton.warpButtonX;
    }

    @Override
    public int warpButtonY() {
        return inventoryButton.warpButtonY;
    }
}
