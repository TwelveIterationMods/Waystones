package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.PlayerWaystonesDataMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.List;

public class WaystoneSyncManager {

    public static void sendWaystonesDataToAll() {
        List<ServerPlayerEntity> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
        for (ServerPlayerEntity player : players) {
            sendWaystonesData(player);
        }
    }

    public static void sendWaystonesData(PlayerEntity player) {
        List<IWaystone> waystones = PlayerWaystoneManager.getWaystones(player);
        long lastInventoryWarp = PlayerWaystoneManager.getLastInventoryWarp(player);
        long lastWarpStoneWarp = PlayerWaystoneManager.getLastWarpStoneWarp(player);
        NetworkHandler.sendTo(new PlayerWaystonesDataMessage(waystones, lastInventoryWarp, lastWarpStoneWarp), player);
    }
}
