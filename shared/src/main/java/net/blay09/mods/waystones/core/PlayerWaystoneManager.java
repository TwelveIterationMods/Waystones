package net.blay09.mods.waystones.core;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.BalmEnvironment;
import net.blay09.mods.waystones.api.*;
import net.blay09.mods.waystones.api.WaystoneTypes;
import net.blay09.mods.waystones.config.DimensionalWarp;
import net.blay09.mods.waystones.config.InventoryButtonMode;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.tag.ModItemTags;
import net.blay09.mods.waystones.worldgen.namegen.NameGenerationMode;
import net.blay09.mods.waystones.worldgen.namegen.NameGenerator;
import net.blay09.mods.waystones.api.ExperienceCost;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class PlayerWaystoneManager {

    private static final Logger logger = LogManager.getLogger();

    private static final IPlayerWaystoneData persistentPlayerWaystoneData = new PersistentPlayerWaystoneData();
    private static final IPlayerWaystoneData inMemoryPlayerWaystoneData = new InMemoryPlayerWaystoneData();

    public static boolean isWaystoneActivated(Player player, IWaystone waystone) {
        return getPlayerWaystoneData(player.level()).isWaystoneActivated(player, waystone);
    }

    public static void activateWaystone(Player player, IWaystone waystone) {
        if (!waystone.hasName() && waystone instanceof IMutableWaystone && waystone.wasGenerated()) {
            NameGenerationMode nameGenerationMode = WaystonesConfig.getActive().worldGen.nameGenerationMode;
            String name = NameGenerator.get(player.getServer()).getName(player.level(), waystone, player.level().random, nameGenerationMode);
            ((IMutableWaystone) waystone).setName(name);
        }

        if (!waystone.hasOwner() && waystone instanceof IMutableWaystone) {
            ((IMutableWaystone) waystone).setOwnerUid(player.getUUID());
        }

        if (player.getServer() != null) {
            WaystoneManager.get(player.getServer()).setDirty();
        }

        if (!isWaystoneActivated(player, waystone) && waystone.getWaystoneType().equals(WaystoneTypes.WAYSTONE)) {
            getPlayerWaystoneData(player.level()).activateWaystone(player, waystone);

            Balm.getEvents().fireEvent(new WaystoneActivatedEvent(player, waystone));
        }
    }

    @Nullable
    public static IWaystone getInventoryButtonTarget(Player player) {
        InventoryButtonMode inventoryButtonMode = WaystonesConfig.getActive().getInventoryButtonMode();
        if (inventoryButtonMode.isReturnToNearest()) {
            return PlayerWaystoneManager.getNearestWaystone(player);
        } else if (inventoryButtonMode.hasNamedTarget()) {
            return WaystoneManager.get(player.getServer()).findWaystoneByName(inventoryButtonMode.getNamedTarget()).orElse(null);
        }

        return null;
    }

    public static boolean canUseInventoryButton(Player player) {
        IWaystone waystone = getInventoryButtonTarget(player);
        final ExperienceCost xpCost = waystone != null ? WaystoneTeleportManager.predictExperienceLevelCost(player,
                waystone,
                WarpMode.INVENTORY_BUTTON,
                null) : ExperienceCost.NoExperienceCost.INSTANCE;
        return getInventoryButtonCooldownLeft(player) <= 0 && xpCost.canAfford(player);
    }

    public static boolean canUseWarpStone(Player player, ItemStack heldItem) {
        return getWarpStoneCooldownLeft(player) <= 0;
    }

    public static double getCooldownMultiplier(IWaystone waystone) {
        return waystone.isGlobal() ? WaystonesConfig.getActive().cooldowns.globalWaystoneCooldownMultiplier : 1f;
    }

    private static void informPlayer(Entity entity, String translationKey) {
        if (entity instanceof Player) {
            var chatComponent = Component.translatable(translationKey);
            chatComponent.withStyle(ChatFormatting.RED);
            ((Player) entity).displayClientMessage(chatComponent, false);
        }
    }

    public static Consumer<WaystoneTeleportError> informRejectedTeleport(final Entity entityToInform) {
        return error -> {
            logger.info("Rejected teleport: " + error.getClass().getSimpleName());
            if (error.getTranslationKey() != null) {
                informPlayer(entityToInform, error.getTranslationKey());
            }
        };
    }

    public static void applyCooldown(WarpMode warpMode, Player player, int cooldown) {
        if (cooldown > 0) {
            final Level level = player.level();
            switch (warpMode) {
                case INVENTORY_BUTTON -> getPlayerWaystoneData(level).setInventoryButtonCooldownUntil(player, System.currentTimeMillis() + cooldown * 1000L);
                case WARP_STONE -> getPlayerWaystoneData(level).setWarpStoneCooldownUntil(player, System.currentTimeMillis() + cooldown * 1000L);
            }
            WaystoneSyncManager.sendWaystoneCooldowns(player);
        }
    }

    public static int getCooldownPeriod(WarpMode warpMode, IWaystone waystone) {
        return (int) (getCooldownPeriod(warpMode) * getCooldownMultiplier(waystone));
    }

    private static int getCooldownPeriod(WarpMode warpMode) {
        return switch (warpMode) {
            case INVENTORY_BUTTON -> WaystonesConfig.getActive().cooldowns.inventoryButtonCooldown;
            case WARP_STONE -> WaystonesConfig.getActive().cooldowns.warpStoneCooldown;
            default -> 0;
        };
    }

    public static boolean canDimensionalWarpBetween(Entity player, IWaystone waystone) {
        ResourceLocation fromDimension = player.level().dimension().location();
        ResourceLocation toDimension = waystone.getDimension().location();
        Collection<String> dimensionAllowList = WaystonesConfig.getActive().restrictions.dimensionalWarpAllowList;
        Collection<String> dimensionDenyList = WaystonesConfig.getActive().restrictions.dimensionalWarpDenyList;
        if (!dimensionAllowList.isEmpty() && (!dimensionAllowList.contains(toDimension.toString()) || !dimensionAllowList.contains(fromDimension.toString()))) {
            return false;
        } else if (!dimensionDenyList.isEmpty() && (dimensionDenyList.contains(toDimension.toString()) || dimensionDenyList.contains(fromDimension.toString()))) {
            return false;
        }

        DimensionalWarp dimensionalWarpMode = WaystonesConfig.getActive().restrictions.dimensionalWarp;
        return dimensionalWarpMode == DimensionalWarp.ALLOW || dimensionalWarpMode == DimensionalWarp.GLOBAL_ONLY && waystone.isGlobal();
    }

    public static ItemStack findWarpItem(Entity entity, WarpMode warpMode) {
        return switch (warpMode) {
            case WARP_SCROLL -> findWarpItem(entity, ModItemTags.WARP_SCROLLS);
            case WARP_STONE -> findWarpItem(entity, ModItemTags.WARP_STONES);
            case RETURN_SCROLL -> findWarpItem(entity, ModItemTags.RETURN_SCROLLS);
            case BOUND_SCROLL -> findWarpItem(entity, ModItemTags.BOUND_SCROLLS);
            default -> ItemStack.EMPTY;
        };
    }

    private static ItemStack findWarpItem(Entity entity, TagKey<Item> warpItemTag) {
        if (entity instanceof LivingEntity livingEntity) {
            if (livingEntity.getMainHandItem().is(warpItemTag)) {
                return livingEntity.getMainHandItem();
            } else if (livingEntity.getOffhandItem().is(warpItemTag)) {
                return livingEntity.getOffhandItem();
            }
        }

        return ItemStack.EMPTY;
    }

    public static void deactivateWaystone(Player player, IWaystone waystone) {
        getPlayerWaystoneData(player.level()).deactivateWaystone(player, waystone);
    }

    public static boolean canUseWarpMode(Entity entity, WarpMode warpMode, ItemStack heldItem, @Nullable IWaystone fromWaystone) {
        return switch (warpMode) {
            case INVENTORY_BUTTON -> entity instanceof Player && PlayerWaystoneManager.canUseInventoryButton(((Player) entity));
            case WARP_SCROLL -> !heldItem.isEmpty() && heldItem.is(ModItemTags.WARP_SCROLLS);
            case BOUND_SCROLL -> !heldItem.isEmpty() && heldItem.is(ModItemTags.BOUND_SCROLLS);
            case RETURN_SCROLL -> !heldItem.isEmpty() && heldItem.is(ModItemTags.RETURN_SCROLLS);
            case WARP_STONE -> !heldItem.isEmpty() && heldItem.is(ModItemTags.WARP_STONES) && entity instanceof Player
                    && PlayerWaystoneManager.canUseWarpStone(((Player) entity), heldItem);
            case WAYSTONE_TO_WAYSTONE -> WaystonesConfig.getActive()
                    .restrictions.allowWaystoneToWaystoneTeleport && fromWaystone != null && fromWaystone.isValid()
                    && fromWaystone.getWaystoneType().equals(WaystoneTypes.WAYSTONE);
            case SHARESTONE_TO_SHARESTONE -> fromWaystone != null && fromWaystone.isValid() && WaystoneTypes.isSharestone(fromWaystone.getWaystoneType());
            case WARP_PLATE -> fromWaystone != null && fromWaystone.isValid() && fromWaystone.getWaystoneType().equals(WaystoneTypes.WARP_PLATE);
            case PORTSTONE_TO_WAYSTONE -> fromWaystone != null && fromWaystone.isValid() && fromWaystone.getWaystoneType().equals(WaystoneTypes.PORTSTONE);
            case CUSTOM -> true;
        };

    }

    public static long getWarpStoneCooldownUntil(Player player) {
        return getPlayerWaystoneData(player.level()).getWarpStoneCooldownUntil(player);
    }

    public static long getWarpStoneCooldownLeft(Player player) {
        long cooldownUntil = getWarpStoneCooldownUntil(player);
        return Math.max(0, cooldownUntil - System.currentTimeMillis());
    }

    public static void setWarpStoneCooldownUntil(Player player, long timeStamp) {
        getPlayerWaystoneData(player.level()).setWarpStoneCooldownUntil(player, timeStamp);
    }

    public static long getInventoryButtonCooldownUntil(Player player) {
        return getPlayerWaystoneData(player.level()).getInventoryButtonCooldownUntil(player);
    }

    public static long getInventoryButtonCooldownLeft(Player player) {
        long cooldownUntil = getInventoryButtonCooldownUntil(player);
        return Math.max(0, cooldownUntil - System.currentTimeMillis());
    }

    public static void setInventoryButtonCooldownUntil(Player player, long timeStamp) {
        getPlayerWaystoneData(player.level()).setInventoryButtonCooldownUntil(player, timeStamp);
    }

    @Nullable
    public static IWaystone getNearestWaystone(Player player) {
        return getPlayerWaystoneData(player.level()).getWaystones(player).stream()
                .filter(it -> it.getDimension() == player.level().dimension())
                .min((first, second) -> {
                    double firstDist = first.getPos().distToCenterSqr(player.getX(), player.getY(), player.getZ());
                    double secondDist = second.getPos().distToCenterSqr(player.getX(), player.getY(), player.getZ());
                    return (int) Math.round(firstDist) - (int) Math.round(secondDist);
                }).orElse(null);
    }

    public static List<IWaystone> getActivatedWaystones(Player player) {
        return getPlayerWaystoneData(player.level()).getWaystones(player);
    }

    public static IPlayerWaystoneData getPlayerWaystoneData(@Nullable Level world) {
        return world == null || world.isClientSide ? inMemoryPlayerWaystoneData : persistentPlayerWaystoneData;
    }

    public static IPlayerWaystoneData getPlayerWaystoneData(BalmEnvironment side) {
        return side.isClient() ? inMemoryPlayerWaystoneData : persistentPlayerWaystoneData;
    }

    public static boolean mayTeleportToWaystone(Player player, IWaystone waystone) {
        return true;
    }

    public static void swapWaystoneSorting(Player player, int index, int otherIndex) {
        getPlayerWaystoneData(player.level()).swapWaystoneSorting(player, index, otherIndex);
    }

    public static boolean mayEditGlobalWaystones(Player player) {
        return player.getAbilities().instabuild || !WaystonesConfig.getActive().restrictions.globalWaystoneSetupRequiresCreativeMode;
    }

    public static void activeWaystoneForEveryone(@Nullable MinecraftServer server, IWaystone waystone) {
        if (server == null) {
            return;
        }

        List<ServerPlayer> players = server.getPlayerList().getPlayers();
        for (ServerPlayer player : players) {
            if (!isWaystoneActivated(player, waystone)) {
                activateWaystone(player, waystone);
            }
        }
    }

    public static void removeKnownWaystone(@Nullable MinecraftServer server, IWaystone waystone) {
        if (server == null) {
            return;
        }

        List<ServerPlayer> players = server.getPlayerList().getPlayers();
        for (ServerPlayer player : players) {
            deactivateWaystone(player, waystone);
            WaystoneSyncManager.sendActivatedWaystones(player);
        }
    }

    public static WaystoneUserSettings getUserSettingsForWaystone(Player player, IWaystone waystone) {
        // TODO intermediate implementation,
        //      should be replaced by a separate storage that can be used for all kinds of waystones instead of relying on the list order
        final var activatedWaystones = PlayerWaystoneManager.getActivatedWaystones(player);
        final var sortIndex = activatedWaystones.indexOf(waystone);
        return new WaystoneUserSettings(sortIndex, WaystoneUserVisibility.DEFAULT);
    }

    public static void updateUserSettingsForWaystone(Player player, IWaystone waystone, WaystoneUserSettings settings) {
    }

    public static List<IWaystone> getTargetsForPlayer(Player player) {
        return PlayerWaystoneManager.getActivatedWaystones(player);
    }

    public static List<IWaystone> getTargetsForItem(Player player, ItemStack itemStack) {
        return PlayerWaystoneManager.getActivatedWaystones(player);
    }

    public static List<IWaystone> getTargetsForWaystone(Player player, IWaystone waystone) {
        final var waystoneType = waystone.getWaystoneType();
        if (WaystoneTypes.isSharestone(waystoneType)) {
            return WaystoneManager.get(player.getServer()).getWaystonesByType(waystoneType).toList();
        }
        return PlayerWaystoneManager.getActivatedWaystones(player);
    }

    public static List<IWaystone> getTargetsForInventoryButton(ServerPlayer player) {
        return PlayerWaystoneManager.getActivatedWaystones(player);
    }
}
