package net.blay09.mods.waystones.api;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface WaystoneManager {

    Optional<Waystone> getWaystoneAt(BlockGetter world, BlockPos pos);

    Optional<Waystone> getWaystoneById(UUID waystoneUid);

    Optional<Waystone> findWaystoneByName(String name);

    Stream<Waystone> getWaystones();

    Stream<Waystone> getWaystonesByType(ResourceLocation type);

    List<Waystone> getGlobalWaystones();
}
