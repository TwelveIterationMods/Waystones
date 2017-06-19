package net.blay09.mods.waystones;

import com.google.common.collect.Maps;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;

public class GlobalWaystones extends WorldSavedData {

	private static final String DATA_NAME = Waystones.MOD_ID + "_GlobalWaystones";
	private static final String TAG_LIST_NAME = "GlobalWaystones";

	private final Map<String, WaystoneEntry> globalWaystones = Maps.newHashMap();

	public GlobalWaystones() {
		super(DATA_NAME);
	}

	public GlobalWaystones(String name) {
		super(name);
	}

	public void addGlobalWaystone(WaystoneEntry entry) {
		globalWaystones.put(entry.getName(), entry);
		markDirty();

		for(EntityPlayer player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
			WaystoneManager.addPlayerWaystone(player, entry);
		}
	}

	public void removeGlobalWaystone(WaystoneEntry entry) {
		globalWaystones.remove(entry.getName());
		markDirty();

		for(EntityPlayer player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
			WaystoneManager.removePlayerWaystone(player, entry);
		}
	}

	public Collection<WaystoneEntry> getGlobalWaystones() {
		return globalWaystones.values();
	}

	@Nullable
	public WaystoneEntry getGlobalWaystone(String name) {
		return globalWaystones.get(name);
	}


	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		NBTTagList tagList = tagCompound.getTagList(TAG_LIST_NAME, Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < tagList.tagCount(); i++) {
			WaystoneEntry entry = WaystoneEntry.read((NBTTagCompound) tagList.get(i));
			globalWaystones.put(entry.getName(), entry);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
		NBTTagList tagList = new NBTTagList();
		for(WaystoneEntry entry : globalWaystones.values()) {
			tagList.appendTag(entry.writeToNBT());
		}
		tagCompound.setTag(TAG_LIST_NAME, tagList);
		return tagCompound;
	}

	public static GlobalWaystones get(World world) {
		MapStorage storage = world.getMapStorage();
		if(storage != null) {
			GlobalWaystones instance = (GlobalWaystones) storage.getOrLoadData(GlobalWaystones.class, DATA_NAME);
			if (instance == null) {
				instance = new GlobalWaystones();
				storage.setData(DATA_NAME, instance);
			}
			return instance;
		}
		return new GlobalWaystones();
	}

}
