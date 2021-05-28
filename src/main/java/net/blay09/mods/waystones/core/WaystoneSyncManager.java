package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.KnownWaystonesMessage;
import net.blay09.mods.waystones.network.message.PlayerWaystoneCooldownsMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class WaystoneSyncManager {

    public static void sendWaystoneUpdateToAll(IWaystone waystone) {
        List<ServerPlayerEntity> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
        for (ServerPlayerEntity player : players) {
            sendWaystoneUpdate(player, waystone);
            sendActivatedWaystones(player);
        }
    }

    public static void sendActivatedWaystones(PlayerEntity player) {
        List<IWaystone> waystones = PlayerWaystoneManager.getWaystones(player);
        NetworkHandler.sendTo(new KnownWaystonesMessage(WaystoneTypes.WAYSTONE, waystones), player);
    }

    public static void sendWarpPlates(PlayerEntity player) {
        List<IWaystone> warpPlates = WaystoneManager.get().getWaystonesByType(WaystoneTypes.WARP_PLATE).collect(Collectors.toList());
        NetworkHandler.sendTo(new KnownWaystonesMessage(WaystoneTypes.WARP_PLATE, warpPlates), player);
    }

    public static void sendWaystoneUpdate(PlayerEntity player, IWaystone waystone) {
        // If this is a waystone, only send an update if the player has activated it already
        if (!waystone.getWaystoneType().equals(WaystoneTypes.WAYSTONE) || PlayerWaystoneManager.isWaystoneActivated(player, waystone)) {
            NetworkHandler.sendTo(new KnownWaystonesMessage(waystone.getWaystoneType(), Collections.singletonList(waystone)), player);
        }
    }

    public static void sendWaystoneCooldowns(PlayerEntity player) {
        long inventoryButtonCooldownUntil = PlayerWaystoneManager.getInventoryButtonCooldownUntil(player);
        long warpStoneCooldownUntil = PlayerWaystoneManager.getWarpStoneCooldownUntil(player);
        NetworkHandler.sendTo(new PlayerWaystoneCooldownsMessage(inventoryButtonCooldownUntil, warpStoneCooldownUntil), player);
    }
}
