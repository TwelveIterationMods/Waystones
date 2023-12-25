package net.blay09.mods.waystones.api.event;

import net.blay09.mods.balm.api.event.BalmEvent;
import net.blay09.mods.waystones.api.Waystone;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * This event is fired on the client side when the client has received a list of waystones of the player.
 * <p>
 * This event will be fired for waystones, warp plates and sharestones.
 * Note that for WaystoneTypes.WAYSTONE, the list will only contain the waystones that the player has discovered.
 */
public class WaystonesListReceivedEvent extends BalmEvent {

    private final ResourceLocation waystoneType;
    private final List<Waystone> waystones;

    public WaystonesListReceivedEvent(ResourceLocation waystoneType, List<Waystone> waystones) {
        this.waystoneType = waystoneType;
        this.waystones = waystones;
    }

    public ResourceLocation getWaystoneType() {
        return waystoneType;
    }

    public List<Waystone> getWaystones() {
        return waystones;
    }
}
