package net.blay09.mods.waystones.handler;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WaystoneManager;
import net.blay09.mods.waystones.core.WaystoneSyncManager;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = Waystones.MOD_ID)
public class LoginHandler {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        // Introduce all global waystones to this player
        List<IWaystone> globalWaystones = WaystoneManager.get().getGlobalWaystones();
        for (IWaystone waystone : globalWaystones) {
            if (!PlayerWaystoneManager.isWaystoneActivated(event.getPlayer(), waystone)) {
                PlayerWaystoneManager.activateWaystone(event.getPlayer(), waystone);
            }
        }

        WaystoneSyncManager.sendWaystonesData(event.getPlayer());
    }

}
