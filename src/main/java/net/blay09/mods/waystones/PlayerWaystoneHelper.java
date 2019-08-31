package net.blay09.mods.waystones;

import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

public class PlayerWaystoneHelper {

    public static final String WAYSTONES = "Waystones";
    public static final String WAYSTONE_LIST = "WaystoneList";
    public static final String LAST_FREE_WARP = "LastFreeWarp";
    public static final String LAST_WARP_STONE_USE = "LastWarpStoneUse";

    public static CompoundNBT getWaystonesTag(PlayerEntity player) {
        return player.getEntityData().getCompound(PlayerEntity.PERSISTED_NBT_TAG).getCompound(WAYSTONES);
    }

    public static CompoundNBT getOrCreateWaystonesTag(PlayerEntity player) {
        CompoundNBT persistedTag = player.getEntityData().getCompound(PlayerEntity.PERSISTED_NBT_TAG);
        CompoundNBT waystonesTag = persistedTag.getCompound(WAYSTONES);
        persistedTag.put(WAYSTONES, waystonesTag);
        player.getEntityData().put(PlayerEntity.PERSISTED_NBT_TAG, persistedTag);
        return waystonesTag;
    }


    public static void store(PlayerEntity player, WaystoneEntry[] entries, long lastFreeWarp, long lastWarpStoneUse) {
        CompoundNBT tagCompound = getOrCreateWaystonesTag(player);
        ListNBT tagList = new ListNBT();
        for (WaystoneEntry entry : entries) {
            tagList.add(entry.writeToNBT());
        }
        tagCompound.put(WAYSTONE_LIST, tagList);
        tagCompound.putLong(LAST_FREE_WARP, lastFreeWarp);
        tagCompound.putLong(LAST_WARP_STONE_USE, lastWarpStoneUse);
    }

    public static boolean canFreeWarp(PlayerEntity player) {
        return System.currentTimeMillis() - getLastFreeWarp(player) > WaystoneConfig.SERVER.teleportButtonCooldown.get() * 1000;
    }

    public static boolean canUseWarpStone(PlayerEntity player) {
        return System.currentTimeMillis() - getLastWarpStoneUse(player) > WaystoneConfig.SERVER.warpStoneCooldown.get() * 1000;
    }

    public static void setLastFreeWarp(PlayerEntity player, long lastFreeWarp) {
        getOrCreateWaystonesTag(player).putLong(LAST_FREE_WARP, lastFreeWarp);
    }

    public static long getLastFreeWarp(PlayerEntity player) {
        return getWaystonesTag(player).getLong(LAST_FREE_WARP);
    }

    public static void setLastWarpStoneUse(PlayerEntity player, long lastWarpStone) {
        getOrCreateWaystonesTag(player).putLong(LAST_WARP_STONE_USE, lastWarpStone);
    }

    public static long getLastWarpStoneUse(PlayerEntity player) {
        return getWaystonesTag(player).getLong(LAST_WARP_STONE_USE);
    }

    @Nullable
    public static WaystoneEntry getLastWaystone(PlayerEntity player) {
        CompoundNBT tagCompound = PlayerWaystoneHelper.getWaystonesTag(player);
        ListNBT tagList = tagCompound.getList(WAYSTONE_LIST, Constants.NBT.TAG_COMPOUND);
        if (tagList.size() > 0) {
            return WaystoneEntry.read(tagList.getCompound(tagList.size() - 1));
        }

        return null;
    }

}
