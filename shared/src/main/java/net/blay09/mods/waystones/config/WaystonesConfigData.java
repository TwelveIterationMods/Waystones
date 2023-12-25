package net.blay09.mods.waystones.config;

import net.blay09.mods.balm.api.config.*;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.worldgen.namegen.NameGenerationMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Config(Waystones.MOD_ID)
public class WaystonesConfigData implements BalmConfigData {

    public enum TransportMobs {
        ENABLED,
        SAME_DIMENSION,
        DISABLED
    }

    public enum VillageWaystoneGeneration {
        DISABLED,
        REGULAR,
        FREQUENT
    }

    public Costs costs = new Costs();
    public Restrictions restrictions = new Restrictions();
    public Cooldowns cooldowns = new Cooldowns();
    public InventoryButton inventoryButton = new InventoryButton();
    public WorldGen worldGen = new WorldGen();
    public Client client = new Client();
    public Compatibility compatibility = new Compatibility();

    public static class Costs {
        @Synced
        @Comment("Set to false to simply disable all xp costs.")
        public boolean enableCosts = true;

        @Synced
        @ExpectedType(String.class)
        @Comment("List of cost modifiers with comma-separated parameters in parentheses. Conditions can be defined as comma-separated list in square brackets. Will be applied in order.")
        public List<String> costModifiers = List.of(
                "[is_not_interdimensional] scaled_add_xp(distance, 0.01)",
                "[is_interdimensional] add_xp(27)",
                "[source_is_warp_plate] multiply_xp(0)",
                "[target_is_global] multiply_xp(0)",
                "min_xp(0)",
                "max_xp(27)",
                "[source_is_inventory_button] add_cooldown(inventory_button, 300)");
    }

    public static class Restrictions {
        @Synced
        @Comment("If enabled, only the owner of a waystone (the one who placed it) can rename it.")
        public boolean restrictEditToOwner = false;

        @Synced
        @Comment("Set to ENABLED to have nearby pets teleport with you. Set to SAME_DIMENSION to have nearby pets teleport with you only if you're not changing dimensions. Set to DISABLED to disable.")
        public TransportMobs transportPets = TransportMobs.SAME_DIMENSION;

        @Synced
        @Comment("Set to ENABLED to have leashed mobs teleport with you. Set to SAME_DIMENSION to have leashed mobs teleport with you only if you're not changing dimensions. Set to DISABLED to disable.")
        public TransportMobs transportLeashed = TransportMobs.ENABLED;

        @Comment("List of leashed mobs that cannot be taken with you when teleporting")
        @ExpectedType(String.class)
        public List<String> leashedDenyList = List.of("minecraft:wither");

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
        @Comment("The time in ticks that it takes to use a warp stone. This is the charge-up time when holding right-click.")
        public int warpStoneUseTime = 32;

        @Synced
        @Comment("The time in ticks that it takes to use a warp plate. This is the time the player has to stand on top for.")
        public int warpPlateUseTime = 15;

        @Synced
        @Comment("The time in ticks it takes to use a scroll. This is the charge-up time when holding right-click.")
        public int scrollUseTime = 32;
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
        public List<String> dimensionAllowList = List.of("minecraft:overworld", "minecraft:the_nether", "minecraft:the_end");

        @Comment("List of dimensions that waystones are not allowed to spawn in through world gen. Only used if worldGenDimensionAllowList is empty.")
        @ExpectedType(String.class)
        public List<String> dimensionDenyList = Collections.emptyList();

        @Comment("Set to 'PRESET_FIRST' to first use names from the custom names list. Set to 'PRESET_ONLY' to use only those custom names. Set to 'MIXED' to have some waystones use custom names, and others random names.")
        public NameGenerationMode nameGenerationMode = NameGenerationMode.PRESET_FIRST;

        @Comment("The template to use when generating new names. Supported placeholders are {Biome} (english biome name) and {MrPork} (the default name generator).")
        public String nameGenerationTemplate = "{MrPork}";

        @Comment("These names will be used for the PRESET name generation mode. See the nameGenerationMode option for more info.")
        @ExpectedType(String.class)
        public List<String> nameGenerationPresets = Collections.emptyList();

        @Comment("Set to REGULAR to have waystones spawn in some villages. Set to FREQUENT to have waystones spawn in most villages. Set to DISABLED to disable waystone generation in villages. Waystones will only spawn in vanilla or supported villages.")
        public VillageWaystoneGeneration spawnInVillages = VillageWaystoneGeneration.REGULAR;
    }

    public static class Compatibility {
        @Comment("If enabled, JourneyMap waypoints will be created for each activated waystone.")
        public boolean displayWaystonesOnJourneyMap = true;

        @Comment("If enabled, JourneyMap waypoints will only be created if the mod 'JourneyMap Integration' is not installed")
        public boolean preferJourneyMapIntegration = true;

        @Comment("If enabled, Waystones will add markers for waystones and sharestones to BlueMap.")
        public boolean blueMapIntegration = true;

        @Comment("If enabled, Waystones will add markers for waystones and sharestones to Dynmap.")
        public boolean dynmapIntegration = true;
    }

    public static class Client {
        @Comment("If enabled, the text overlay on waystones will no longer always render at full brightness.")
        public boolean disableTextGlow = false;
    }

    public InventoryButtonMode getInventoryButtonMode() {
        return new InventoryButtonMode(inventoryButton.inventoryButton);
    }
}
