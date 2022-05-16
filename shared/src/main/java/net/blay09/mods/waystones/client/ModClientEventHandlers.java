package net.blay09.mods.waystones.client;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.client.FovUpdateEvent;
import net.blay09.mods.waystones.api.KnownWaystonesEvent;
import net.blay09.mods.waystones.api.WaystoneUpdateReceivedEvent;
import net.blay09.mods.waystones.compat.JourneyMapAddon;
import net.blay09.mods.waystones.handler.WarpStoneFOVHandler;

public class ModClientEventHandlers {
    public static void initialize() {
        Balm.getEvents().onEvent(FovUpdateEvent.class, WarpStoneFOVHandler::onFOV);
        Balm.getEvents().onEvent(KnownWaystonesEvent.class, (event)-> {
            if(JourneyMapAddon.getInstance() != null) {
                JourneyMapAddon.getInstance().onKnownWaystones(event);
            }
        });
        Balm.getEvents().onEvent(WaystoneUpdateReceivedEvent.class, (event)-> {
            if(JourneyMapAddon.getInstance() != null) {
                JourneyMapAddon.getInstance().onWaystoneUpdateReceived(event);
            }
        });
    }
}
