package net.blay09.mods.waystones.handler;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.config.WaystoneConfig;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WaystoneManager;
import net.blay09.mods.waystones.core.WaystoneSyncManager;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.SyncConfigMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Mod.EventBusSubscriber(modid = Waystones.MOD_ID)
public class LoginHandler {

    private static final Logger logger = LogManager.getLogger();

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        // Introduce all global waystones to this player
        List<IWaystone> globalWaystones = WaystoneManager.get().getGlobalWaystones();
        for (IWaystone waystone : globalWaystones) {
            if (!PlayerWaystoneManager.isWaystoneActivated(event.getPlayer(), waystone)) {
                PlayerWaystoneManager.activateWaystone(event.getPlayer(), waystone);
            }
        }

        WaystoneSyncManager.sendKnownWaystones(event.getPlayer());
        WaystoneSyncManager.sendWaystoneCooldowns(event.getPlayer());

        syncServerConfigs(event.getPlayer());
    }

    private static void syncServerConfigs(PlayerEntity player) {
        try {
            final byte[] configData = Files.readAllBytes(WaystoneConfig.getServerConfigPath());
            NetworkHandler.sendTo(new SyncConfigMessage(configData), player);
        } catch (IOException e) {
            logger.error("Failed to sync Waystones config", e);
        }
    }

}
