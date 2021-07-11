package net.blay09.mods.waystones.handler;

import net.blay09.mods.balm.event.BalmEvents;
import net.blay09.mods.balm.event.client.BalmClientEvents;
import net.blay09.mods.waystones.ModEvents;
import net.blay09.mods.waystones.api.WaystoneActivatedEvent;

public class ModEventHandlers {
    public static void initialize() {
        BalmClientEvents.onFovUpdate(WarpStoneFOVHandler::onFOV);

        BalmEvents.onPlayerLogin(LoginHandler::onPlayerLogin);
        BalmEvents.onLivingDamage(WarpDamageResetHandler::onDamage);
        ModEvents.WAYSTONE_ACTIVATED.register(WaystoneActivationStatHandler::onWaystoneActivated);
    }
}
