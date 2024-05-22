package net.blay09.mods.waystones.api.event;

import net.blay09.mods.balm.api.event.BalmEvent;
import net.blay09.mods.waystones.core.WaystoneManagerImpl;

public class WaystonesLoadedEvent extends BalmEvent {
    private final WaystoneManagerImpl waystoneManager;

    public WaystonesLoadedEvent(WaystoneManagerImpl waystoneManager) {
        this.waystoneManager = waystoneManager;
    }

    public WaystoneManagerImpl getWaystoneManager() {
        return waystoneManager;
    }
}
