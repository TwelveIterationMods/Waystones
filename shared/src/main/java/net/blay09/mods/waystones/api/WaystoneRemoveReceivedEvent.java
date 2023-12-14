package net.blay09.mods.waystones.api;

import net.blay09.mods.balm.api.event.BalmEvent;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

/**
 * This event is fired on the client side when the client has been notified of a waystone being removed.
 */
public class WaystoneRemoveReceivedEvent extends BalmEvent {
    private final ResourceLocation waystoneType;
    private final UUID waystoneId;
    private final boolean wasDestroyed;

    public WaystoneRemoveReceivedEvent(ResourceLocation waystoneType, UUID waystoneId, boolean wasDestroyed) {
        this.waystoneType = waystoneType;
        this.waystoneId = waystoneId;
        this.wasDestroyed = wasDestroyed;
    }

    public ResourceLocation getWaystoneType() {
        return waystoneType;
    }

    public UUID getWaystoneId() {
        return waystoneId;
    }

    /**
     * @return true if the waystone was destroyed, i.e. it is not just being moved with silk touch
     */
    public boolean wasDestroyed() {
        return wasDestroyed;
    }
}
