
package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.IWaystone;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraftforge.common.util.Constants;

import java.util.*;

public class PersistentPlayerWaystoneData implements IPlayerWaystoneData {
    private static final String TAG_NAME = "WaystonesData";
    private static final String ACTIVATED_WAYSTONES = "Waystones";
    private static final String INVENTORY_BUTTON_COOLDOWN_UNTIL = "InventoryButtonCooldownUntilNew";
    private static final String WARP_STONE_COOLDOWN_UNTIL = "WarpStoneCooldownUntilNew";

    @Override
    public void activateWaystone(PlayerEntity player, IWaystone waystone) {
        ListNBT activatedWaystonesData = getActivatedWaystonesData(getWaystonesData(player));
        activatedWaystonesData.add(0, StringNBT.valueOf(waystone.getWaystoneUid().toString()));
    }

    @Override
    public boolean isWaystoneActivated(PlayerEntity player, IWaystone waystone) {
        ListNBT activatedWaystones = getActivatedWaystonesData(getWaystonesData(player));
        String waystoneUid = waystone.getWaystoneUid().toString();
        for (INBT activatedWaystone : activatedWaystones) {
            if (waystoneUid.equals(activatedWaystone.getString())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public List<IWaystone> getWaystones(PlayerEntity player) {
        ListNBT activatedWaystones = getActivatedWaystonesData(getWaystonesData(player));
        List<IWaystone> waystones = new ArrayList<>();
        for (Iterator<INBT> iterator = activatedWaystones.iterator(); iterator.hasNext(); ) {
            INBT activatedWaystone = iterator.next();
            WaystoneProxy proxy = new WaystoneProxy(UUID.fromString(activatedWaystone.getString()));
            if (proxy.isValid()) {
                waystones.add(proxy);
            } else {
                iterator.remove();
            }
        }

        return waystones;
    }

    @Override
    public void swapWaystoneSorting(PlayerEntity player, int index, int otherIndex) {
        ListNBT activatedWaystones = getActivatedWaystonesData(getWaystonesData(player));
        if (otherIndex == -1) {
            INBT waystone = activatedWaystones.remove(index);
            activatedWaystones.add(0, waystone);
        } else if (otherIndex == activatedWaystones.size()) {
            INBT waystone = activatedWaystones.remove(index);
            activatedWaystones.add(waystone);
        } else {
            Collections.swap(activatedWaystones, index, otherIndex);
        }
    }

    @Override
    public void deactivateWaystone(PlayerEntity player, IWaystone waystone) {
        CompoundNBT data = getWaystonesData(player);
        ListNBT activatedWaystones = getActivatedWaystonesData(data);
        String waystoneUid = waystone.getWaystoneUid().toString();
        for (int i = activatedWaystones.size() - 1; i >= 0; i--) {
            INBT activatedWaystone = activatedWaystones.get(i);
            if (waystoneUid.equals(activatedWaystone.getString())) {
                activatedWaystones.remove(i);
                break;
            }
        }
    }

    @Override
    public long getWarpStoneCooldownUntil(PlayerEntity player) {
        return getWaystonesData(player).getLong(WARP_STONE_COOLDOWN_UNTIL);
    }

    @Override
    public void setWarpStoneCooldownUntil(PlayerEntity player, long timeStamp) {
        getWaystonesData(player).putLong(WARP_STONE_COOLDOWN_UNTIL, timeStamp);
    }

    @Override
    public long getInventoryButtonCooldownUntil(PlayerEntity player) {
        return getWaystonesData(player).getLong(INVENTORY_BUTTON_COOLDOWN_UNTIL);
    }

    @Override
    public void setInventoryButtonCooldownUntil(PlayerEntity player, long timeStamp) {
        getWaystonesData(player).putLong(INVENTORY_BUTTON_COOLDOWN_UNTIL, timeStamp);
    }

    private static ListNBT getActivatedWaystonesData(CompoundNBT data) {
        ListNBT list = data.getList(ACTIVATED_WAYSTONES, Constants.NBT.TAG_STRING);
        data.put(ACTIVATED_WAYSTONES, list);
        return list;
    }

    private static CompoundNBT getWaystonesData(PlayerEntity player) {
        CompoundNBT playerData = player.getPersistentData();
        CompoundNBT persistedData = playerData.getCompound(PlayerEntity.PERSISTED_NBT_TAG);
        CompoundNBT compound = persistedData.getCompound(TAG_NAME);
        persistedData.put(TAG_NAME, compound);
        playerData.put(PlayerEntity.PERSISTED_NBT_TAG, persistedData);
        return compound;
    }
}
