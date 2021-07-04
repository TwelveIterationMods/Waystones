package net.blay09.mods.waystones.handler;

import net.blay09.mods.forbic.event.ForbicEvents;
import net.blay09.mods.waystones.ModEvents;
import net.blay09.mods.waystones.api.WaystoneActivatedEvent;

public class ModEventHandlers {
    public static void initialize() {
        ForbicEvents.onPlayerLogin(LoginHandler::onPlayerLogin);
        ForbicEvents.onFovUpdate(WarpStoneFOVHandler::onFOV);
        ForbicEvents.onLivingDamage(WarpDamageResetHandler::onDamage);
        ModEvents.WAYSTONE_ACTIVATED.register(WaystoneActivationStatHandler::onWaystoneActivated);
    }
}
