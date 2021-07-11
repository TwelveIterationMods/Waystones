package net.blay09.mods.waystones.handler;

import net.blay09.mods.balm.network.BalmNetworking;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WaystoneManager;
import net.blay09.mods.waystones.core.WaystoneSyncManager;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class LoginHandler {

    public static void onPlayerLogin(ServerPlayer player) {
        // Introduce all global waystones to this player
        List<IWaystone> globalWaystones = WaystoneManager.get(player.server).getGlobalWaystones();
        for (IWaystone waystone : globalWaystones) {
            if (!PlayerWaystoneManager.isWaystoneActivated(player, waystone)) {
                PlayerWaystoneManager.activateWaystone(player, waystone);
            }
        }

        WaystoneSyncManager.sendActivatedWaystones(player);
        WaystoneSyncManager.sendWarpPlates(player);
        WaystoneSyncManager.sendWaystoneCooldowns(player);

        BalmNetworking.sendTo(player, WaystonesConfig.getConfigSyncMessage());
    }

}
