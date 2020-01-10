package net.blay09.mods.waystones.handler;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.PlayerWaystonesDataMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = Waystones.MOD_ID)
public class LoginSyncHandler {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        List<IWaystone> waystones = PlayerWaystoneManager.getWaystones(player);
        long lastInventoryWarp = PlayerWaystoneManager.getLastInventoryWarp(player);
        long lastWarpStoneWarp = PlayerWaystoneManager.getLastWarpStoneWarp(player);
        NetworkHandler.sendTo(new PlayerWaystonesDataMessage(waystones, lastInventoryWarp, lastWarpStoneWarp), player);
    }

}
