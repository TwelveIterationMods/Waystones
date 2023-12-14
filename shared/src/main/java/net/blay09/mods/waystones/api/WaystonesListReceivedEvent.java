package net.blay09.mods.waystones.api;

import net.blay09.mods.balm.api.event.BalmEvent;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * This event is fired on the client side when the client has received a list of waystones of the player.
 * <p>
 * Note that for WaystoneTypes.WAYSTONE, the list will only contain the waystones that the player has discovered.
 */
public class WaystonesListReceivedEvent extends BalmEvent {

    private final ResourceLocation waystoneType;
    private final List<IWaystone> waystones;

    public WaystonesListReceivedEvent(ResourceLocation waystoneType, List<IWaystone> waystones) {
        this.waystoneType = waystoneType;
        this.waystones = waystones;
    }

    public ResourceLocation getWaystoneType() {
        return waystoneType;
    }

    public List<IWaystone> getWaystones() {
        return waystones;
    }
}
