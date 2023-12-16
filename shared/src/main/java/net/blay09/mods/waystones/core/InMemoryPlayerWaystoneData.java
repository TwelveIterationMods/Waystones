package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.IWaystone;
import net.minecraft.world.entity.player.Player;

import java.util.*;

public class InMemoryPlayerWaystoneData implements IPlayerWaystoneData {
    private final List<UUID> sortingIndex = new ArrayList<>();
    private final Map<UUID, IWaystone> waystones = new HashMap<>();
    private long warpStoneCooldownUntil;
    private long inventoryButtonCooldownUntil;

    @Override
    public void activateWaystone(Player player, IWaystone waystone) {
        waystones.put(waystone.getWaystoneUid(), waystone);
        sortingIndex.add(waystone.getWaystoneUid());
    }

    @Override
    public boolean isWaystoneActivated(Player player, IWaystone waystone) {
        return waystones.containsKey(waystone.getWaystoneUid());
    }

    @Override
    public void deactivateWaystone(Player player, IWaystone waystone) {
        waystones.remove(waystone.getWaystoneUid());
        sortingIndex.remove(waystone.getWaystoneUid());
    }

    @Override
    public long getWarpStoneCooldownUntil(Player player) {
        return warpStoneCooldownUntil;
    }

    @Override
    public void setWarpStoneCooldownUntil(Player player, long timeStamp) {
        warpStoneCooldownUntil = timeStamp;
    }

    @Override
    public long getInventoryButtonCooldownUntil(Player player) {
        return inventoryButtonCooldownUntil;
    }

    @Override
    public void setInventoryButtonCooldownUntil(Player player, long timeStamp) {
        inventoryButtonCooldownUntil = timeStamp;
    }

    @Override
    public Collection<IWaystone> getWaystones(Player player) {
        return waystones.values();
    }

    @Override
    public void sortWaystoneAsFirst(Player player, UUID waystoneUid) {
        sortingIndex.remove(waystoneUid);
        sortingIndex.add(0, waystoneUid);
    }

    @Override
    public void sortWaystoneAsLast(Player player, UUID waystoneUid) {
        sortingIndex.remove(waystoneUid);
        sortingIndex.add(waystoneUid);
    }

    @Override
    public void sortWaystoneSwap(Player player, UUID waystoneUid, UUID otherWaystoneUid) {
        final var waystoneIndex = sortingIndex.indexOf(waystoneUid);
        final var otherWaystoneIndex = sortingIndex.indexOf(otherWaystoneUid);
        if (waystoneIndex != -1 && otherWaystoneIndex != -1) {
            Collections.swap(sortingIndex, waystoneIndex, otherWaystoneIndex);
        }
    }

    @Override
    public List<UUID> getSortingIndex(Player player) {
        return sortingIndex;
    }

    @Override
    public void setSortingIndex(Player player, List<UUID> sortingIndex) {
        this.sortingIndex.clear();
        this.sortingIndex.addAll(sortingIndex);
    }

    public void setWaystones(Collection<IWaystone> waystones) {
        this.waystones.clear();
        for (final var waystone : waystones) {
            this.waystones.put(waystone.getWaystoneUid(), waystone);
        }
    }
}
