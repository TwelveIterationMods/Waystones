package net.blay09.mods.waystones.api;

import net.blay09.mods.forbic.event.ForbicEvent;

public class GenerateWaystoneNameEvent extends ForbicEvent {

    private final IWaystone waystone;
    private String name;

    public GenerateWaystoneNameEvent(IWaystone waystone, String name) {
        this.waystone = waystone;
        this.name = name;
    }

    public IWaystone getWaystone() {
        return waystone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
