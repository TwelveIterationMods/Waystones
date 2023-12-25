package net.blay09.mods.waystones.core;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.BalmEnvironment;
import net.blay09.mods.waystones.api.*;
import net.blay09.mods.waystones.api.WaystoneTypes;
import net.blay09.mods.waystones.api.error.WaystoneTeleportError;
import net.blay09.mods.waystones.api.event.WaystoneActivatedEvent;
import net.blay09.mods.waystones.cost.NoCost;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.blay09.mods.waystones.config.DimensionalWarp;
import net.blay09.mods.waystones.config.InventoryButtonMode;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.worldgen.namegen.NameGenerationMode;
import net.blay09.mods.waystones.worldgen.namegen.NameGenerator;
import net.blay09.mods.waystones.api.cost.Cost;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class PlayerWaystoneManager {

    private static final Logger logger = LogManager.getLogger();

    private static final IPlayerWaystoneData persistentPlayerWaystoneData = new PersistentPlayerWaystoneData();
    private static final IPlayerWaystoneData inMemoryPlayerWaystoneData = new InMemoryPlayerWaystoneData();

    public static boolean isWaystoneActivated(Player player, Waystone waystone) {
        return getPlayerWaystoneData(player.level()).isWaystoneActivated(player, waystone);
    }

    public static void activateWaystone(Player player, Waystone waystone) {
        if (!waystone.hasName() && waystone instanceof MutableWaystone && waystone.wasGenerated()) {
            NameGenerationMode nameGenerationMode = WaystonesConfig.getActive().worldGen.nameGenerationMode;
            String name = NameGenerator.get(player.getServer()).getName(player.level(), waystone, player.level().random, nameGenerationMode);
            ((MutableWaystone) waystone).setName(name);
        }

        if (!waystone.hasOwner() && waystone instanceof MutableWaystone) {
            ((MutableWaystone) waystone).setOwnerUid(player.getUUID());
        }

        if (player.getServer() != null) {
            WaystoneManagerImpl.get(player.getServer()).setDirty();
        }

        if (!isWaystoneActivated(player, waystone) && waystone.getWaystoneType().equals(WaystoneTypes.WAYSTONE)) {
            getPlayerWaystoneData(player.level()).activateWaystone(player, waystone);

            Balm.getEvents().fireEvent(new WaystoneActivatedEvent(player, waystone));
        }
    }

    @Nullable
    public static Waystone getInventoryButtonTarget(Player player) {
        InventoryButtonMode inventoryButtonMode = WaystonesConfig.getActive().getInventoryButtonMode();
        if (inventoryButtonMode.isReturnToNearest()) {
            return PlayerWaystoneManager.getNearestWaystone(player);
        } else if (inventoryButtonMode.hasNamedTarget()) {
            return WaystoneManagerImpl.get(player.getServer()).findWaystoneByName(inventoryButtonMode.getNamedTarget()).orElse(null);
        }

        return null;
    }

    public static boolean canUseInventoryButton(Player player) {
        Waystone waystone = getInventoryButtonTarget(player);
        final Cost xpCost = waystone != null ? WaystoneTeleportManager.predictExperienceLevelCost(player,
                waystone,
                null) : NoCost.INSTANCE;
        return getInventoryButtonCooldownLeft(player) <= 0 && xpCost.canAfford(player);
    }

    public static boolean canUseWarpStone(Player player, ItemStack heldItem) {
        return getWarpStoneCooldownLeft(player) <= 0;
    }

    public static double getCooldownMultiplier(Waystone waystone) {
        return waystone.getVisibility() == WaystoneVisibility.GLOBAL ? WaystonesConfig.getActive().cooldowns.globalWaystoneCooldownMultiplier : 1f;
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

    public static void applyInventoryButtonCooldown(Player player, int cooldown) {
        if (cooldown > 0) {
            final Level level = player.level();
            getPlayerWaystoneData(level).setInventoryButtonCooldownUntil(player, System.currentTimeMillis() + cooldown * 1000L);
            WaystoneSyncManager.sendWaystoneCooldowns(player);
        }
    }

    public static int getInventoryButtonCooldownPeriod(Waystone waystone) {
        return (int) (getInventoryButtonCooldownPeriod() * getCooldownMultiplier(waystone));
    }

    private static int getInventoryButtonCooldownPeriod() {
        return WaystonesConfig.getActive().cooldowns.inventoryButtonCooldown;
    }

    public static boolean canDimensionalWarpBetween(Entity player, Waystone waystone) {
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
        return dimensionalWarpMode == DimensionalWarp.ALLOW || dimensionalWarpMode == DimensionalWarp.GLOBAL_ONLY && waystone.getVisibility() == WaystoneVisibility.GLOBAL;
    }

    public static void deactivateWaystone(Player player, Waystone waystone) {
        getPlayerWaystoneData(player.level()).deactivateWaystone(player, waystone);
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
    public static Waystone getNearestWaystone(Player player) {
        return getPlayerWaystoneData(player.level()).getWaystones(player).stream()
                .filter(it -> it.getDimension() == player.level().dimension())
                .min((first, second) -> {
                    double firstDist = first.getPos().distToCenterSqr(player.getX(), player.getY(), player.getZ());
                    double secondDist = second.getPos().distToCenterSqr(player.getX(), player.getY(), player.getZ());
                    return (int) Math.round(firstDist) - (int) Math.round(secondDist);
                }).orElse(null);
    }

    public static Collection<Waystone> getActivatedWaystones(Player player) {
        return getPlayerWaystoneData(player.level()).getWaystones(player);
    }

    public static IPlayerWaystoneData getPlayerWaystoneData(@Nullable Level world) {
        return world == null || world.isClientSide ? inMemoryPlayerWaystoneData : persistentPlayerWaystoneData;
    }

    public static IPlayerWaystoneData getPlayerWaystoneData(BalmEnvironment side) {
        return side.isClient() ? inMemoryPlayerWaystoneData : persistentPlayerWaystoneData;
    }

    public static boolean mayTeleportToWaystone(Player player, Waystone waystone) {
        return true;
    }

    public static List<UUID> getSortingIndex(Player player) {
        return getPlayerWaystoneData(player.level()).getSortingIndex(player);
    }

    public static List<UUID> ensureSortingIndex(Player player, Collection<Waystone> waystones) {
        return getPlayerWaystoneData(player.level()).ensureSortingIndex(player, waystones);
    }

    public static void sortWaystoneAsFirst(Player player, UUID waystoneUid) {
        getPlayerWaystoneData(player.level()).sortWaystoneAsFirst(player, waystoneUid);
    }

    public static void sortWaystoneAsLast(Player player, UUID waystoneUid) {
        getPlayerWaystoneData(player.level()).sortWaystoneAsLast(player, waystoneUid);
    }

    public static void sortWaystoneSwap(Player player, UUID waystoneUid, UUID otherWaystoneUid) {
        getPlayerWaystoneData(player.level()).sortWaystoneSwap(player, waystoneUid, otherWaystoneUid);
    }

    public static void activeWaystoneForEveryone(@Nullable MinecraftServer server, Waystone waystone) {
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

    public static void removeKnownWaystone(@Nullable MinecraftServer server, Waystone waystone) {
        if (server == null) {
            return;
        }

        List<ServerPlayer> players = server.getPlayerList().getPlayers();
        for (ServerPlayer player : players) {
            deactivateWaystone(player, waystone);
            WaystoneSyncManager.sendActivatedWaystones(player);
        }
    }

    public static Collection<Waystone> getTargetsForPlayer(Player player) {
        return PlayerWaystoneManager.getActivatedWaystones(player);
    }

    public static Collection<Waystone> getTargetsForItem(Player player, ItemStack itemStack) {
        return PlayerWaystoneManager.getActivatedWaystones(player);
    }

    public static Collection<Waystone> getTargetsForWaystone(Player player, Waystone waystone) {
        final var waystoneType = waystone.getWaystoneType();
        final var result = new ArrayList<Waystone>();
        if (WaystoneTypes.isSharestone(waystoneType)) {
            result.addAll(WaystoneManagerImpl.get(player.getServer()).getWaystonesByType(waystoneType).toList());
        } else {
            result.addAll(PlayerWaystoneManager.getActivatedWaystones(player));
        }

        final var blockEntity = player.level().getBlockEntity(waystone.getPos());
        if (blockEntity instanceof WaystoneBlockEntityBase waystoneBlockEntity) {
            result.addAll(waystoneBlockEntity.getAuxiliaryTargets());
        }

        return result;
    }

    public static Collection<Waystone> getTargetsForInventoryButton(ServerPlayer player) {
        return PlayerWaystoneManager.getActivatedWaystones(player);
    }
}
