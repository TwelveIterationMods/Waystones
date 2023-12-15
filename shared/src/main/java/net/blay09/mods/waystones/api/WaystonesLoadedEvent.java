package net.blay09.mods.waystones.api;

import net.blay09.mods.balm.api.event.BalmEvent;
import net.blay09.mods.waystones.core.WaystoneManager;

public class WaystonesLoadedEvent extends BalmEvent {
    private final WaystoneManager waystoneManager;

    public WaystonesLoadedEvent(WaystoneManager waystoneManager) {
        this.waystoneManager = waystoneManager;
    }

    public WaystoneManager getWaystoneManager() {
        return waystoneManager;
    }
}
