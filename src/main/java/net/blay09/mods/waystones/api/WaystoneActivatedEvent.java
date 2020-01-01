package net.blay09.mods.waystones.api;

import net.minecraftforge.eventbus.api.Event;

public class WaystoneActivatedEvent extends Event {
    private final IWaystone waystone;

    public WaystoneActivatedEvent(IWaystone waystone) {
        this.waystone = waystone;
    }

    public IWaystone getWaystone() {
        return waystone;
    }
}
