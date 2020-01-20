package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.IWaystone;
import net.minecraft.entity.player.PlayerEntity;

import java.util.*;

public class InMemoryPlayerWaystoneData implements IPlayerWaystoneData {
    private final List<IWaystone> sortedWaystones = new ArrayList<>();
    private final Map<UUID, IWaystone> waystones = new HashMap<>();
    private long warpStoneCooldownUntil;
    private long inventoryButtonCooldownUntil;

    @Override
    public void activateWaystone(PlayerEntity player, IWaystone waystone) {
        waystones.put(waystone.getWaystoneUid(), waystone);
        sortedWaystones.add(0, waystone);
    }

    @Override
    public boolean isWaystoneActivated(PlayerEntity player, IWaystone waystone) {
        return waystones.containsKey(waystone.getWaystoneUid());
    }

    @Override
    public void deactivateWaystone(PlayerEntity player, IWaystone waystone) {
        waystones.remove(waystone.getWaystoneUid());
        sortedWaystones.remove(waystone);
    }

    @Override
    public long getWarpStoneCooldownUntil(PlayerEntity player) {
        return warpStoneCooldownUntil;
    }

    @Override
    public void setWarpStoneCooldownUntil(PlayerEntity player, long timeStamp) {
        warpStoneCooldownUntil = timeStamp;
    }

    @Override
    public long getInventoryButtonCooldownUntil(PlayerEntity player) {
        return inventoryButtonCooldownUntil;
    }

    @Override
    public void setInventoryButtonCooldownUntil(PlayerEntity player, long timeStamp) {
        inventoryButtonCooldownUntil = timeStamp;
    }

    @Override
    public List<IWaystone> getWaystones(PlayerEntity player) {
        return sortedWaystones;
    }

    @Override
    public void swapWaystoneSorting(PlayerEntity player, int index, int otherIndex) {
        Collections.swap(sortedWaystones, index, otherIndex);
    }

    public void setWaystones(List<IWaystone> waystones) {
        this.sortedWaystones.clear();
        this.waystones.clear();
        this.sortedWaystones.addAll(waystones);
        for (IWaystone waystone : waystones) {
            this.waystones.put(waystone.getWaystoneUid(), waystone);
        }
    }
}
