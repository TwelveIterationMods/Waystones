package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.IWaystone;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InMemoryPlayerWaystoneData implements IPlayerWaystoneData {
    private final Map<UUID, IWaystone> waystones = new HashMap<>();
    private long lastWarpStoneWarp;
    private long lastInventoryWarp;

    @Override
    public void activateWaystone(PlayerEntity player, IWaystone waystone) {
        waystones.put(waystone.getWaystoneUid(), waystone);
    }

    @Override
    public boolean isWaystoneActivated(PlayerEntity player, IWaystone waystone) {
        return waystones.containsKey(waystone.getWaystoneUid());
    }

    @Override
    public void deactivateWaystone(PlayerEntity player, IWaystone waystone) {
        waystones.remove(waystone.getWaystoneUid());
    }

    @Override
    public long getLastWarpStoneWarp(PlayerEntity player) {
        return lastWarpStoneWarp;
    }

    @Override
    public void setLastWarpStoneWarp(PlayerEntity player, long timeStamp) {
        lastWarpStoneWarp = timeStamp;
    }

    @Override
    public long getLastInventoryWarp(PlayerEntity player) {
        return lastInventoryWarp;
    }

    @Override
    public void setLastInventoryWarp(PlayerEntity player, long timeStamp) {
        lastInventoryWarp = timeStamp;
    }

    @Override
    public Collection<IWaystone> getWaystones(PlayerEntity player) {
        return waystones.values();
    }

    public void setWaystones(Collection<IWaystone> waystones) {
        this.waystones.clear();
        for (IWaystone waystone : waystones) {
            this.waystones.put(waystone.getWaystoneUid(), waystone);
        }
    }
}
