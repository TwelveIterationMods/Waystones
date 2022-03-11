package net.blay09.mods.waystones.api;

import net.blay09.mods.balm.api.event.BalmEvent;
import net.minecraft.world.entity.player.Player;

/**
 * This event is fired on MinecraftForge.EVENT_BUS on the client side when the client has received an update to a waystone.
 */
public class WaystoneUpdateReceivedEvent extends BalmEvent {
    private final IWaystone waystone;

    public WaystoneUpdateReceivedEvent(IWaystone waystone) {
        this.waystone = waystone;
    }

    public IWaystone getWaystone() {
        return waystone;
    }
}
