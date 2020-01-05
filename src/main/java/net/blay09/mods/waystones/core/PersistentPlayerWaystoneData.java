package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.IWaystone;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PersistentPlayerWaystoneData implements IPlayerWaystoneData {
    private static final String TAG_NAME = "WaystonesData";
    private static final String ACTIVATED_WAYSTONES = "Waystones";
    private static final String LAST_INVENTORY_WARP = "LastInventoryWarp";
    private static final String LAST_WARPSTONE_WARP = "LastWarpStoneWarp";

    @Override
    public void activateWaystone(PlayerEntity player, IWaystone waystone) {
        CompoundNBT activatedWaystonesData = getActivatedWaystonesData(getWaystonesData(player));
        activatedWaystonesData.putBoolean(waystone.getWaystoneUid().toString(), true);
    }

    @Override
    public boolean isWaystoneActivated(PlayerEntity player, IWaystone waystone) {
        return getActivatedWaystonesData(getWaystonesData(player)).contains(waystone.getWaystoneUid().toString());
    }

    @Override
    public List<IWaystone> getWaystones(PlayerEntity player) {
        CompoundNBT activatedWaystonesData = getActivatedWaystonesData(getWaystonesData(player));
        List<IWaystone> waystones = new ArrayList<>();
        for (String waystoneUid : activatedWaystonesData.keySet()) {
            WaystoneProxy proxy = new WaystoneProxy(UUID.fromString(waystoneUid));
            if (proxy.isValid()) {
                waystones.add(proxy);
            }
        }

        return waystones;
    }

    @Override
    public void deactivateWaystone(PlayerEntity player, IWaystone waystone) {
        CompoundNBT data = getWaystonesData(player);
        CompoundNBT activatedWaystonesData = getActivatedWaystonesData(data);
        activatedWaystonesData.remove(waystone.getWaystoneUid().toString());
    }

    @Override
    public long getLastWarpStoneWarp(PlayerEntity player) {
        return getWaystonesData(player).getLong(LAST_WARPSTONE_WARP);
    }

    @Override
    public void setLastWarpStoneWarp(PlayerEntity player, long timeStamp) {
        getWaystonesData(player).putLong(LAST_WARPSTONE_WARP, timeStamp);
    }

    @Override
    public long getLastInventoryWarp(PlayerEntity player) {
        return getWaystonesData(player).getLong(LAST_INVENTORY_WARP);
    }

    @Override
    public void setLastInventoryWarp(PlayerEntity player, long timeStamp) {
        getWaystonesData(player).putLong(LAST_INVENTORY_WARP, timeStamp);
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
}
