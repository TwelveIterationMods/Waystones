package net.blay09.mods.waystones.api;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface IWaystoneManager {

    Optional<IWaystone> getWaystoneAt(BlockGetter world, BlockPos pos);

    Optional<IWaystone> getWaystoneById(UUID waystoneUid);

    Optional<IWaystone> findWaystoneByName(String name);

    Stream<IWaystone> getWaystones();

    Stream<IWaystone> getWaystonesByType(ResourceLocation type);

    List<IWaystone> getGlobalWaystones();
}
