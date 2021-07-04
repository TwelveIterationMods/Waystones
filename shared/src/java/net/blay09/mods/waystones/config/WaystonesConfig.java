package net.blay09.mods.waystones.config;

import net.blay09.mods.forbic.config.ForbicConfig;
import net.blay09.mods.forbic.event.ForbicEvents;
import net.blay09.mods.forbic.network.ForbicNetworking;
import net.blay09.mods.forbic.network.SyncConfigMessage;
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
        return ForbicConfig.getConfig(WaystonesConfigData.class);
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
        setActiveConfig(ForbicConfig.initialize(WaystonesConfigData.class));

        ForbicEvents.onServerStarted(currentServer::set);
        ForbicEvents.onServerStopped(server -> {
            currentServer.set(null);
        });

        ForbicEvents.onConfigReloaded(() -> {
            if (currentServer.get() != null) {
                ForbicNetworking.sendToAll(currentServer.get(), getConfigSyncMessage());
            }
        });
    }
}
