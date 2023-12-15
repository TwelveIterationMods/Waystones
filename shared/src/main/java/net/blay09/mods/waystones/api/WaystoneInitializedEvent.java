package net.blay09.mods.waystones.api;

import net.blay09.mods.balm.api.event.BalmEvent;

public class WaystoneInitializedEvent extends BalmEvent {
    private final IWaystone waystone;

    public WaystoneInitializedEvent(IWaystone waystone) {
        this.waystone = waystone;
    }

    public IWaystone getWaystone() {
        return waystone;
    }
}
