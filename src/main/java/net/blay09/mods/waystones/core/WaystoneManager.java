package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.tileentity.WaystoneTileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class WaystoneManager extends WorldSavedData {

    private static final String DATA_NAME = Waystones.MOD_ID;
    private static final String TAG_WAYSTONES = "Waystones";
    private static final WaystoneManager clientStorageCopy = new WaystoneManager();

    private final Map<UUID, IWaystone> waystones = new HashMap<>();

    public WaystoneManager() {
        super(DATA_NAME);
    }

    public void addWaystone(IWaystone waystone) {
        waystones.put(waystone.getWaystoneUid(), waystone);
    }

    public void removeWaystone(IWaystone waystone) {
        waystones.remove(waystone.getWaystoneUid());
    }

    public Optional<IWaystone> getWaystoneAt(IBlockReader world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof WaystoneTileEntity) {
            return Optional.of(((WaystoneTileEntity) tileEntity).getWaystone());
        }

        return Optional.empty();
    }

    public Optional<IWaystone> getWaystoneById(UUID waystoneUid) {
        return Optional.ofNullable(waystones.get(waystoneUid));
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        ListNBT tagList = tagCompound.getList(TAG_WAYSTONES, Constants.NBT.TAG_COMPOUND);
        for (INBT tag : tagList) {
            CompoundNBT compound = (CompoundNBT) tag;
            UUID waystoneUid = NBTUtil.readUniqueId(compound.getCompound("WaystoneUid"));
            String name = compound.getString("Name");
            DimensionType dimensionType = DimensionType.getById(compound.getInt("DimensionTypeId"));
            if (dimensionType == null) {
                dimensionType = DimensionType.OVERWORLD;
            }
            BlockPos pos = NBTUtil.readBlockPos(compound.getCompound("BlockPos"));
            boolean wasGenerated = compound.getBoolean("WasGenerated");
            UUID ownerUid = compound.contains("OwnerUid") ? NBTUtil.readUniqueId(compound.getCompound("OwnerUid")) : null;
            Waystone waystone = new Waystone(waystoneUid, name, dimensionType, pos, wasGenerated, ownerUid);
            waystone.setGlobal(compound.getBoolean("IsGlobal"));
            waystones.put(waystoneUid, waystone);
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        ListNBT tagList = new ListNBT();
        for (IWaystone waystone : waystones.values()) {
            CompoundNBT compound = new CompoundNBT();
            compound.put("WaystoneUid", NBTUtil.writeUniqueId(waystone.getWaystoneUid()));
            compound.putString("Name", waystone.getName());
            compound.putInt("DimensionTypeId", waystone.getDimensionType().getId());
            compound.put("BlockPos", NBTUtil.writeBlockPos(waystone.getPos()));
            compound.putBoolean("WasGenerated", waystone.wasGenerated());
            if (waystone.getOwnerUid() != null) {
                compound.put("OwnerUid", NBTUtil.writeUniqueId(waystone.getOwnerUid()));
            }
            compound.putBoolean("IsGlobal", waystone.isGlobal());
            tagList.add(compound);
        }
        tagCompound.put(TAG_WAYSTONES, tagList);
        return tagCompound;
    }

    public static WaystoneManager get() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            ServerWorld overworld = server.getWorld(DimensionType.OVERWORLD);
            DimensionSavedDataManager storage = overworld.getSavedData();
            return storage.getOrCreate(WaystoneManager::new, DATA_NAME);
        }

        return clientStorageCopy;
    }
}
