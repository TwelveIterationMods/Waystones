package net.blay09.mods.waystones.api.event;

import net.blay09.mods.balm.api.event.BalmEvent;
import net.blay09.mods.waystones.api.Waystone;

public class GenerateWaystoneNameEvent extends BalmEvent {

    private final Waystone waystone;
    private String name;

    public GenerateWaystoneNameEvent(Waystone waystone, String name) {
        this.waystone = waystone;
        this.name = name;
    }

    public Waystone getWaystone() {
        return waystone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
