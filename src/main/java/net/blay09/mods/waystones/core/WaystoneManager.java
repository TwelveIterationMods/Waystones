package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.tileentity.WaystoneTileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import java.util.Optional;
import java.util.UUID;

public class WaystoneManager extends WorldSavedData {

    private static final String DATA_NAME = Waystones.MOD_ID;
    private static final String TAG_WAYSTONES = "Waystones";
    private static final WaystoneManager clientStorageCopy = new WaystoneManager();

    public WaystoneManager() {
        super(DATA_NAME);
    }

    public static void removeWaystone(IWaystone waystone) {
//        WaystoneEntry entry = new WaystoneEntry(waystone);
//        if (waystone.isGlobal()) {
//            GlobalWaystones.get(world).removeGlobalWaystone(entry);
//        }
//        for (PlayerEntity player : world.getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB(pos).grow(64, 64, 64))) {
//            WaystoneManagerLegacy.removePlayerWaystone(player, entry);
//            WaystoneManagerLegacy.sendPlayerWaystones(player);
//        }
    }

    public static Optional<IWaystone> getWaystoneAt(IBlockReader world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof WaystoneTileEntity) {
            return Optional.of(((WaystoneTileEntity) tileEntity).getWaystone());
        }

        return Optional.empty();
    }

    public static Optional<IWaystone> getWaystoneById(UUID waystoneUid) {
        return Optional.empty();
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        ListNBT tagList = tagCompound.getList(TAG_WAYSTONES, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tagList.size(); i++) {
            //TODO WaystoneEntry entry = WaystoneEntry.read((CompoundNBT) tagList.get(i));
            //globalWaystones.put(entry.getName(), entry);
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        ListNBT tagList = new ListNBT();
        tagCompound.put(TAG_WAYSTONES, tagList);
        return tagCompound;
    }

    public static WaystoneManager get(World world) {
        MinecraftServer server = world.getServer();
        if (server != null) {
            ServerWorld overworld = server.getWorld(DimensionType.OVERWORLD);
            DimensionSavedDataManager storage = overworld.getSavedData();
            return storage.getOrCreate(WaystoneManager::new, DATA_NAME);
        }

        return clientStorageCopy;
    }
}
