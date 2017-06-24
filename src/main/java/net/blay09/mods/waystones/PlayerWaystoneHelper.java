package net.blay09.mods.waystones;

import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

public class PlayerWaystoneHelper {

	public static final String WAYSTONES = "Waystones";
	public static final String WAYSTONE_LIST = "WaystoneList";
	public static final String LAST_FREE_WARP = "LastFreeWarp";
	public static final String LAST_WARP_STONE_USE = "LastWarpStoneUse";

	public static NBTTagCompound getWaystonesTag(EntityPlayer player) {
		return player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getCompoundTag(WAYSTONES);
	}

	public static NBTTagCompound getOrCreateWaystonesTag(EntityPlayer player) {
		NBTTagCompound persistedTag = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
		NBTTagCompound waystonesTag = persistedTag.getCompoundTag(WAYSTONES);
		persistedTag.setTag(WAYSTONES, waystonesTag);
		player.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, persistedTag);
		return waystonesTag;
	}


	public static void store(EntityPlayer player, WaystoneEntry[] entries, long lastFreeWarp, long lastWarpStoneUse) {
		NBTTagCompound tagCompound = getOrCreateWaystonesTag(player);
		NBTTagList tagList = new NBTTagList();
		for(WaystoneEntry entry : entries) {
			tagList.appendTag(entry.writeToNBT());
		}
		tagCompound.setTag(WAYSTONE_LIST, tagList);
		tagCompound.setLong(LAST_FREE_WARP, lastFreeWarp);
		tagCompound.setLong(LAST_WARP_STONE_USE, lastWarpStoneUse);
	}

	public static boolean canFreeWarp(EntityPlayer player) {
		return System.currentTimeMillis() - getLastFreeWarp(player) > WaystoneConfig.general.teleportButtonCooldown * 1000;
	}

	public static boolean canUseWarpStone(EntityPlayer player) {
		return System.currentTimeMillis() - getLastWarpStoneUse(player) > WaystoneConfig.general.warpStoneCooldown * 1000;
	}

	public static void setLastFreeWarp(EntityPlayer player, long lastFreeWarp) {
		getOrCreateWaystonesTag(player).setLong(LAST_FREE_WARP, lastFreeWarp);
	}

	public static long getLastFreeWarp(EntityPlayer player) {
		return getWaystonesTag(player).getLong(LAST_FREE_WARP);
	}

	public static void setLastWarpStoneUse(EntityPlayer player, long lastWarpStone) {
		getOrCreateWaystonesTag(player).setLong(LAST_WARP_STONE_USE, lastWarpStone);
	}

	public static long getLastWarpStoneUse(EntityPlayer player) {
		return getWaystonesTag(player).getLong(LAST_WARP_STONE_USE);
	}

	@Nullable
	public static WaystoneEntry getLastWaystone(EntityPlayer player) {
		NBTTagCompound tagCompound = PlayerWaystoneHelper.getWaystonesTag(player);
		NBTTagList tagList = tagCompound.getTagList(WAYSTONE_LIST, Constants.NBT.TAG_COMPOUND);
		if (tagList.tagCount() > 0) {
			return WaystoneEntry.read(tagList.getCompoundTagAt(tagList.tagCount() - 1));
		}
		return null;
	}

}
