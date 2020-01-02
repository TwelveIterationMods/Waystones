package net.blay09.mods.waystones.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.Event;

public class WaystoneActivatedEvent extends Event {
    private final PlayerEntity player;
    private final IWaystone waystone;

    public WaystoneActivatedEvent(PlayerEntity player, IWaystone waystone) {
        this.player = player;
        this.waystone = waystone;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public IWaystone getWaystone() {
        return waystone;
    }
}
