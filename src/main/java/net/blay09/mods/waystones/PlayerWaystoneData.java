package net.blay09.mods.waystones;

import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

public class PlayerWaystoneData {

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

	public long getLastFreeWarp() {
		return lastFreeWarp;
	}

	public long getLastWarpStoneUse() {
		return lastWarpStoneUse;
	}

	public void store(EntityPlayerMP player) {
		PlayerWaystoneHelper.store(player, entries, lastFreeWarp, lastWarpStoneUse);
	}

	public static PlayerWaystoneData fromPlayer(EntityPlayer player) {
		NBTTagCompound tagCompound = PlayerWaystoneHelper.getWaystonesTag(player);
		NBTTagList tagList = tagCompound.getTagList(PlayerWaystoneHelper.WAYSTONE_LIST, Constants.NBT.TAG_COMPOUND);
		WaystoneEntry[] entries = new WaystoneEntry[tagList.tagCount()];
		for (int i = 0; i < entries.length; i++) {
			entries[i] = WaystoneEntry.read(tagList.getCompoundTagAt(i));
		}
		long lastFreeWarp = tagCompound.getLong(PlayerWaystoneHelper.LAST_FREE_WARP);
		long lastWarpStoneUse = tagCompound.getLong(PlayerWaystoneHelper.LAST_WARP_STONE_USE);
		return new PlayerWaystoneData(entries, lastFreeWarp, lastWarpStoneUse);
	}

}
