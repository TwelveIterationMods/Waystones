package net.blay09.mods.waystones.core;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import java.util.Optional;
import java.util.UUID;

public class WaystoneManager {

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
        return Optional.empty();
    }

    public static Optional<IWaystone> getWaystoneById(UUID waystoneUid) {
        return Optional.empty();
    }
}
