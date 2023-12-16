package net.blay09.mods.waystones.core;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.*;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WaystoneManager extends SavedData implements IWaystoneManager {

    private static final String DATA_NAME = Waystones.MOD_ID;
    private static final String TAG_WAYSTONES = "Waystones";
    private static final WaystoneManager clientStorageCopy = new WaystoneManager();

    private final Map<UUID, IWaystone> waystones = new HashMap<>();

    public void addWaystone(IWaystone waystone) {
        waystones.put(waystone.getWaystoneUid(), waystone);
        setDirty();
        Balm.getEvents().fireEvent(new WaystoneInitializedEvent(waystone));
    }

    public void updateWaystone(IWaystone waystone) {
        Waystone mutableWaystone = (Waystone) waystones.getOrDefault(waystone.getWaystoneUid(), waystone);
        mutableWaystone.setName(waystone.getName());
        mutableWaystone.setVisibility(waystone.getVisibility());
        waystones.put(waystone.getWaystoneUid(), mutableWaystone);
        setDirty();
        Balm.getEvents().fireEvent(new WaystoneUpdatedEvent(waystone));
    }

    public void removeWaystone(IWaystone waystone) {
        waystones.remove(waystone.getWaystoneUid());
        setDirty();
        Balm.getEvents().fireEvent(new WaystoneRemovedEvent(waystone));
    }

    @Override
    public Optional<IWaystone> getWaystoneAt(BlockGetter world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof WaystoneBlockEntityBase) {
            return Optional.of(((WaystoneBlockEntityBase) blockEntity).getWaystone());
        }

        return Optional.empty();
    }

    @Override
    public Optional<IWaystone> getWaystoneById(UUID waystoneUid) {
        return Optional.ofNullable(waystones.get(waystoneUid));
    }

    @Override
    public Optional<IWaystone> findWaystoneByName(String name) {
        return waystones.values().stream().filter(it -> it.getName().equals(name)).findFirst();
    }

    @Override
    public Stream<IWaystone> getWaystones() {
        return waystones.values().stream();
    }

    @Override
    public Stream<IWaystone> getWaystonesByType(ResourceLocation type) {
        return waystones.values().stream()
                .filter(it -> it.getWaystoneType().equals(type))
                .sorted(Comparator.comparing(IWaystone::getName)); // TODO this shouldn't sort here
    }

    @Override
    public List<IWaystone> getGlobalWaystones() {
        return waystones.values().stream().filter(it -> it.getVisibility() == WaystoneVisibility.GLOBAL).collect(Collectors.toList());
    }

    public static WaystoneManager read(CompoundTag tagCompound) {
        WaystoneManager waystoneManager = new WaystoneManager();
        ListTag tagList = tagCompound.getList(TAG_WAYSTONES, Tag.TAG_COMPOUND);
        for (Tag tag : tagList) {
            CompoundTag compound = (CompoundTag) tag;
            IWaystone waystone = Waystone.read(compound);
            waystoneManager.waystones.put(waystone.getWaystoneUid(), waystone);
        }
        Balm.getEvents().fireEvent(new WaystonesLoadedEvent(waystoneManager));
        return waystoneManager;
    }

    @Override
    public CompoundTag save(CompoundTag tagCompound) {
        ListTag tagList = new ListTag();
        for (IWaystone waystone : waystones.values()) {
            tagList.add(Waystone.write(waystone, new CompoundTag()));
        }
        tagCompound.put(TAG_WAYSTONES, tagList);
        return tagCompound;
    }

    public static WaystoneManager get(@Nullable MinecraftServer server) {
        if (server != null) {
            ServerLevel overworld = server.getLevel(Level.OVERWORLD);
            return Objects.requireNonNull(overworld).getDataStorage().computeIfAbsent(new Factory<>(WaystoneManager::new, WaystoneManager::read,
                    DataFixTypes.SAVED_DATA_MAP_DATA), DATA_NAME); // TODO this is most likely wrong but I don't think Forge has a solution, Fabric allows null
        }

        return clientStorageCopy;
    }
}
