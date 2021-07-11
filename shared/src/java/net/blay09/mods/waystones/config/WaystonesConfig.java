package net.blay09.mods.waystones.config;

import net.blay09.mods.balm.config.BalmConfig;
import net.blay09.mods.balm.event.BalmEvents;
import net.blay09.mods.balm.network.BalmNetworking;
import net.blay09.mods.balm.network.SyncConfigMessage;
import net.blay09.mods.waystones.network.message.SyncWaystonesConfigMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;

import java.util.concurrent.atomic.AtomicReference;

public class WaystonesConfig {

    private static final AtomicReference<MinecraftServer> currentServer = new AtomicReference<>();
    private static WaystonesConfigData activeConfig;

    public static WaystonesConfigData getActive() {
        return activeConfig;
    }

    public static WaystonesConfigData getFallback() {
        return BalmConfig.getConfig(WaystonesConfigData.class);
    }

    public static SyncConfigMessage<WaystonesConfigData> getConfigSyncMessage() {
        return new SyncWaystonesConfigMessage(getFallback());
    }

    public static void handleSync(Player player, SyncConfigMessage<WaystonesConfigData> message) {
        setActiveConfig(message.getData());
    }

    public static void setActiveConfig(WaystonesConfigData config) {
        activeConfig = config;
    }

    public static void initialize() {
        setActiveConfig(BalmConfig.initialize(WaystonesConfigData.class));

        BalmEvents.onServerStarted(currentServer::set);
        BalmEvents.onServerStopped(server -> {
            currentServer.set(null);
        });

        BalmEvents.onConfigReloaded(() -> {
            if (currentServer.get() != null) {
                BalmNetworking.sendToAll(currentServer.get(), getConfigSyncMessage());
            }
        });
    }
}
