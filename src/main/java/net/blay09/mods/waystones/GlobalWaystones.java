package net.blay09.mods.waystones;

import com.google.common.collect.Maps;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;

public class GlobalWaystones extends WorldSavedData {

    private static final String DATA_NAME = Waystones.MOD_ID + "_GlobalWaystones";
    private static final String TAG_LIST_NAME = "GlobalWaystones";

	private static final GlobalWaystones clientStorageCopy = new GlobalWaystones();
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

        for (PlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            WaystoneManager.addPlayerWaystone(player, entry);
        }
    }

    public void removeGlobalWaystone(WaystoneEntry entry) {
        globalWaystones.remove(entry.getName());
        markDirty();

        for (PlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
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
    public void read(CompoundNBT tagCompound) {
        ListNBT tagList = tagCompound.getList(TAG_LIST_NAME, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tagList.size(); i++) {
            WaystoneEntry entry = WaystoneEntry.read((CompoundNBT) tagList.get(i));
            globalWaystones.put(entry.getName(), entry);
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        ListNBT tagList = new ListNBT();
        for (WaystoneEntry entry : globalWaystones.values()) {
            tagList.add(entry.writeToNBT());
        }
        tagCompound.put(TAG_LIST_NAME, tagList);
        return tagCompound;
    }

    public static GlobalWaystones get(World world) {
        MinecraftServer server = world.getServer();
        if (server != null) {
            ServerWorld overworld = server.getWorld(DimensionType.OVERWORLD);
            DimensionSavedDataManager storage = overworld.getSavedData();
            return storage.getOrCreate(GlobalWaystones::new, DATA_NAME);
        }

        return clientStorageCopy;
    }

}
