package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.IWaystone;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;

import java.util.Collection;

public interface IPlayerWaystoneData {
    void activateWaystone(PlayerEntity player, IWaystone waystone);
    boolean isWaystoneActivated(PlayerEntity player, IWaystone waystone);
    void deactivateWaystone(PlayerEntity player, IWaystone waystone);
    long getLastWarpStoneWarp(PlayerEntity player);
    void setLastWarpStoneWarp(PlayerEntity player, long timeStamp);
    long getLastInventoryWarp(PlayerEntity player);
    void setLastInventoryWarp(PlayerEntity player, long timeStamp);
    Collection<IWaystone> getWaystones(PlayerEntity player);
}
