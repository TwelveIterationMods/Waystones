package net.blay09.mods.waystones.config;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.SyncConfigMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Mod.EventBusSubscriber(modid = Waystones.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class WaystonesConfig {

    private static final Logger logger = LogManager.getLogger();

    public static final ForgeConfigSpec commonSpec;
    public static final WaystoneCommonConfig COMMON;

    static {
        final Pair<WaystoneCommonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(WaystoneCommonConfig::new);
        commonSpec = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public static final ForgeConfigSpec serverSpec;
    public static final WaystoneServerConfig SERVER;

    public static Path getServerConfigPath() {
        return FMLPaths.CONFIGDIR.get().resolve(Waystones.MOD_ID + "-server.toml").toAbsolutePath();
    }

    static {
        final Pair<WaystoneServerConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(WaystoneServerConfig::new);
        serverSpec = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    public static final ForgeConfigSpec clientSpec;
    public static final WaystoneClientConfig CLIENT;

    static {
        final Pair<WaystoneClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(WaystoneClientConfig::new);
        clientSpec = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    public static InventoryButtonMode getInventoryButtonMode() {
        return new InventoryButtonMode(SERVER.inventoryButton.get());
    }

    @SubscribeEvent
    public static void onConfigReloaded(ModConfig.Reloading event) {
        if (event.getConfig().getType() == ModConfig.Type.SERVER) {
            final MinecraftServer currentServer = ServerLifecycleHooks.getCurrentServer();
            if (currentServer != null) {
                final PlayerList playerList = currentServer.getPlayerList();
                //noinspection ConstantConditions
                if (playerList != null) {
                    for (ServerPlayerEntity player : playerList.getPlayers()) {
                        syncServerConfigs(player);
                    }
                }
            }
        }
    }

    public static void syncServerConfigs(PlayerEntity player) {
        try {
            final byte[] configData = Files.readAllBytes(WaystonesConfig.getServerConfigPath());
            NetworkHandler.sendTo(new SyncConfigMessage(configData), player);
        } catch (IOException e) {
            logger.error("Failed to sync Waystones config", e);
        }
    }
}
