package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.IWaystone;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface IPlayerWaystoneData {
    void activateWaystone(Player player, IWaystone waystone);
    boolean isWaystoneActivated(Player player, IWaystone waystone);
    void deactivateWaystone(Player player, IWaystone waystone);
    long getWarpStoneCooldownUntil(Player player);
    void setWarpStoneCooldownUntil(Player player, long timeStamp);
    long getInventoryButtonCooldownUntil(Player player);
    void setInventoryButtonCooldownUntil(Player player, long timeStamp);
    List<UUID> getSortingIndex(Player player);
    void setSortingIndex(Player player, List<UUID> sortingIndex);
    Collection<IWaystone> getWaystones(Player player);
    void sortWaystoneAsFirst(Player player, UUID waystoneUid);
    void sortWaystoneAsLast(Player player, UUID waystoneUid);
    void sortWaystoneSwap(Player player, UUID waystoneUid, UUID otherWaystoneUid);
}
