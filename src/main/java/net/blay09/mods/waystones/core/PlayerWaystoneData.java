package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.IWaystone;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

import java.util.Collections;
import java.util.List;

public class PlayerWaystoneData {
    private static final String TAG_NAME = "WaystonesData";
    private static final String ACTIVATED_WAYSTONES = "Waystones";
    private static final String LAST_INVENTORY_WARP = "LastInventoryWarp";
    private static final String LAST_WARPSTONE_WARP = "LastWarpStoneWarp";

    public static void activateWaystone(PlayerEntity player, IWaystone waystone) {
        CompoundNBT data = getWaystonesData(player);
        CompoundNBT activatedWaystonesData = getActivatedWaystonesData(data);
        activatedWaystonesData.putBoolean(waystone.getWaystoneUid().toString(), true);
    }

    public static boolean isWaystoneActivated(PlayerEntity player, IWaystone waystone) {
        return getActivatedWaystonesData(getWaystonesData(player)).contains(waystone.getWaystoneUid().toString());
    }

    public static void deactivateWaystone(PlayerEntity player, IWaystone entry) {
        CompoundNBT data = getWaystonesData(player);
        CompoundNBT activatedWaystonesData = getActivatedWaystonesData(data);
        activatedWaystonesData.remove(entry.getWaystoneUid().toString());
    }

    private static CompoundNBT getActivatedWaystonesData(CompoundNBT data) {
        CompoundNBT compound = data.getCompound(ACTIVATED_WAYSTONES);
        data.put(ACTIVATED_WAYSTONES, compound);
        return compound;
    }

    private static CompoundNBT getWaystonesData(PlayerEntity player) {
        CompoundNBT compound = player.getPersistentData().getCompound(TAG_NAME);
        player.getPersistentData().put(TAG_NAME, compound);
        return compound;
    }

    public static void setLastInventoryWarp(PlayerEntity player, long timeStamp) {
        getWaystonesData(player).putLong(LAST_INVENTORY_WARP, timeStamp);
    }

    public static void setLastWarpStoneWarp(PlayerEntity player, long timeStamp) {
        getWaystonesData(player).putLong(LAST_WARPSTONE_WARP, timeStamp);
    }

    public static long getLastInventoryWarp(PlayerEntity player) {
        return getWaystonesData(player).getLong(LAST_INVENTORY_WARP);
    }

    public static long getLastWarpStoneWarp(PlayerEntity player) {
        return getWaystonesData(player).getLong(LAST_WARPSTONE_WARP);
    }

    public static List<IWaystone> getWaystones(PlayerEntity player) {
        return Collections.emptyList(); // TODO implement me
    }
}
