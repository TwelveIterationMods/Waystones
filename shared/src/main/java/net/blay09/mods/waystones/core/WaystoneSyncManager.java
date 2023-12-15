package net.blay09.mods.waystones.core;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.api.WaystoneTypes;
import net.blay09.mods.waystones.network.message.KnownWaystonesMessage;
import net.blay09.mods.waystones.network.message.PlayerWaystoneCooldownsMessage;
import net.blay09.mods.waystones.network.message.UpdateWaystoneMessage;
import net.blay09.mods.waystones.network.message.WaystoneRemovedMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class WaystoneSyncManager {

    public static void sendWaystoneUpdateToAll(@Nullable MinecraftServer server, IWaystone waystone) {
        if (server == null) {
            return;
        }

        List<ServerPlayer> players = server.getPlayerList().getPlayers();
        for (ServerPlayer player : players) {
            sendWaystoneUpdate(player, waystone);
            sendActivatedWaystones(player);
        }
    }

    public static void sendWaystoneRemovalToAll(@Nullable MinecraftServer server, IWaystone waystone, boolean wasDestroyed) {
        if (server == null) {
            return;
        }

        List<ServerPlayer> players = server.getPlayerList().getPlayers();
        for (ServerPlayer player : players) {
            sendWaystoneRemoval(player, waystone, wasDestroyed);
        }
    }

    public static void sendActivatedWaystones(Player player) {
        List<IWaystone> waystones = PlayerWaystoneManager.getWaystones(player);
        Balm.getNetworking().sendTo(player, new KnownWaystonesMessage(WaystoneTypes.WAYSTONE, waystones));
    }

    public static void sendWaystonesOfType(ResourceLocation waystoneType, ServerPlayer player) {
        List<IWaystone> warpPlates = WaystoneManager.get(player.server).getWaystonesByType(waystoneType).collect(Collectors.toList());
        Balm.getNetworking().sendTo(player, new KnownWaystonesMessage(waystoneType, warpPlates));
    }

    public static void sendWaystoneUpdate(Player player, IWaystone waystone) {
        // If this is a waystone, only send an update if the player has activated it already
        if (!waystone.getWaystoneType().equals(WaystoneTypes.WAYSTONE) || PlayerWaystoneManager.isWaystoneActivated(player, waystone)) {
            Balm.getNetworking().sendTo(player, new UpdateWaystoneMessage(waystone));
        }
    }

    public static void sendWaystoneRemoval(Player player, IWaystone waystone, boolean wasDestroyed) {
        // If this is a waystone, only send an update if the player has activated it already
        if (!waystone.getWaystoneType().equals(WaystoneTypes.WAYSTONE) || PlayerWaystoneManager.isWaystoneActivated(player, waystone)) {
            Balm.getNetworking().sendTo(player, new WaystoneRemovedMessage(waystone.getWaystoneType(), waystone.getWaystoneUid(), wasDestroyed));
        }
    }

    public static void sendWaystoneCooldowns(Player player) {
        long inventoryButtonCooldownUntil = PlayerWaystoneManager.getInventoryButtonCooldownUntil(player);
        long warpStoneCooldownUntil = PlayerWaystoneManager.getWarpStoneCooldownUntil(player);
        Balm.getNetworking().sendTo(player, new PlayerWaystoneCooldownsMessage(inventoryButtonCooldownUntil, warpStoneCooldownUntil));
    }
}
