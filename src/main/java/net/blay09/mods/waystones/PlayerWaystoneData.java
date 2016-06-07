package net.blay09.mods.waystones;

import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

public class PlayerWaystoneData {

	public static final String WAYSTONES = "Waystones";
	public static final String WAYSTONE_LIST = "WaystoneList";
	public static final String LAST_FREE_WARP = "LastFreeWarp";
	public static final String LAST_WARP_STONE_USE = "LastWarpStoneUse";

	private final WaystoneEntry[] entries;
	private final long lastFreeWarp;
	private final long lastWarpStoneUse;

	public PlayerWaystoneData(WaystoneEntry[] entries, long lastFreeWarp, long lastWarpStoneUse) {
		this.entries = entries;
		this.lastFreeWarp = lastFreeWarp;
		this.lastWarpStoneUse = lastWarpStoneUse;
	}

	public WaystoneEntry[] getWaystones() {
		return entries;
	}

	public WaystoneEntry getLastWaystone() {
		return entries.length > 0 ? entries[entries.length - 1] : null;
	}

	public long getLastFreeWarp() {
		return lastFreeWarp;
	}

	public long getLastWarpStoneUse() {
		return lastWarpStoneUse;
	}

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

	public static PlayerWaystoneData fromPlayer(EntityPlayer player) {
		NBTTagCompound tagCompound = getWaystonesTag(player);
		NBTTagList tagList = tagCompound.getTagList(WAYSTONE_LIST, Constants.NBT.TAG_COMPOUND);
		WaystoneEntry[] entries = new WaystoneEntry[tagList.tagCount()];
		for (int i = 0; i < entries.length; i++) {
			entries[i] = WaystoneEntry.read(tagList.getCompoundTagAt(i));
		}
		long lastFreeWarp = tagCompound.getLong(LAST_FREE_WARP);
		long lastWarpStoneUse = tagCompound.getLong(LAST_WARP_STONE_USE);
		return new PlayerWaystoneData(entries, lastFreeWarp, lastWarpStoneUse);
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

	public static WaystoneEntry getLastWaystone(EntityPlayer player) {
		NBTTagList tagList = getWaystonesTag(player).getTagList(WAYSTONE_LIST, Constants.NBT.TAG_COMPOUND);
		if (tagList.tagCount() > 0) {
			return WaystoneEntry.read(tagList.getCompoundTagAt(tagList.tagCount() - 1));
		}
		return null;
	}

	public static boolean canFreeWarp(EntityPlayer player) {
		return System.currentTimeMillis() - getLastFreeWarp(player) > Waystones.getConfig().teleportButtonCooldown * 1000;
	}

	public static boolean canUseWarpStone(EntityPlayer player) {
		return System.currentTimeMillis() - getLastWarpStoneUse(player) > Waystones.getConfig().warpStoneCooldown * 1000;
	}

	public static void setLastFreeWarp(EntityPlayer player, long lastFreeWarp) {
		PlayerWaystoneData.getOrCreateWaystonesTag(player).setLong(PlayerWaystoneData.LAST_FREE_WARP, lastFreeWarp);
	}

	public static long getLastFreeWarp(EntityPlayer player) {
		return PlayerWaystoneData.getWaystonesTag(player).getLong(PlayerWaystoneData.LAST_FREE_WARP);
	}

	public static void setLastWarpStoneUse(EntityPlayer player, long lastWarpStone) {
		PlayerWaystoneData.getOrCreateWaystonesTag(player).setLong(PlayerWaystoneData.LAST_WARP_STONE_USE, lastWarpStone);
	}

	public static long getLastWarpStoneUse(EntityPlayer player) {
		return PlayerWaystoneData.getWaystonesTag(player).getLong(PlayerWaystoneData.LAST_WARP_STONE_USE);
	}
}
