package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.IMutableWaystone;
import net.blay09.mods.waystones.api.IWaystone;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class Waystone implements IWaystone, IMutableWaystone {

    private final ResourceLocation waystoneType;
    private final UUID waystoneUid;
    private final boolean wasGenerated;

    private ResourceKey<Level> dimension;
    private BlockPos pos;

    private String name = "";
    private boolean isGlobal;

    private UUID ownerUid;

    public Waystone(ResourceLocation waystoneType, UUID waystoneUid, ResourceKey<Level> dimension, BlockPos pos, boolean wasGenerated, @Nullable UUID ownerUid) {
        this.waystoneType = waystoneType;
        this.waystoneUid = waystoneUid;
        this.dimension = dimension;
        this.pos = pos;
        this.wasGenerated = wasGenerated;
        this.ownerUid = ownerUid;
    }

    @Override
    public UUID getWaystoneUid() {
        return waystoneUid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public ResourceKey<Level> getDimension() {
        return dimension;
    }

    @Override
    public boolean wasGenerated() {
        return wasGenerated;
    }

    @Override
    public boolean isGlobal() {
        return isGlobal;
    }

    @Override
    public void setGlobal(boolean global) {
        isGlobal = global;
    }

    @Override
    public boolean isOwner(Player player) {
        return ownerUid == null || player.getGameProfile().getId().equals(ownerUid) || player.getAbilities().instabuild;
    }

    @Override
    public void setOwnerUid(@Nullable UUID ownerUid) {
        this.ownerUid = ownerUid;
    }

    @Override
    public BlockPos getPos() {
        return pos;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public UUID getOwnerUid() {
        return ownerUid;
    }

    public void setDimension(ResourceKey<Level> dimension) {
        this.dimension = dimension;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public ResourceLocation getWaystoneType() {
        return waystoneType;
    }

    public static IWaystone read(FriendlyByteBuf buf) {
        UUID waystoneUid = buf.readUUID();
        ResourceLocation waystoneType = buf.readResourceLocation();
        String name = buf.readUtf();
        boolean isGlobal = buf.readBoolean();
        ResourceKey<Level> dimension = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(buf.readUtf(250)));
        BlockPos pos = buf.readBlockPos();

        Waystone waystone = new Waystone(waystoneType, waystoneUid, dimension, pos, false, null);
        waystone.setName(name);
        waystone.setGlobal(isGlobal);
        return waystone;
    }

    public static IWaystone read(CompoundTag compound) {
        UUID waystoneUid = NbtUtils.loadUUID(Objects.requireNonNull(compound.get("WaystoneUid")));
        String name = compound.getString("Name");
        ResourceKey<Level> dimensionType = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(compound.getString("World")));
        BlockPos pos = NbtUtils.readBlockPos(compound.getCompound("BlockPos"));
        boolean wasGenerated = compound.getBoolean("WasGenerated");
        UUID ownerUid = compound.contains("OwnerUid") ? NbtUtils.loadUUID(Objects.requireNonNull(compound.get("OwnerUid"))) : null;
        ResourceLocation waystoneType = compound.contains("Type") ? new ResourceLocation(compound.getString("Type")) : WaystoneTypes.WAYSTONE;
        Waystone waystone = new Waystone(waystoneType, waystoneUid, dimensionType, pos, wasGenerated, ownerUid);
        waystone.setName(name);
        waystone.setGlobal(compound.getBoolean("IsGlobal"));
        return waystone;
    }

    public static void write(FriendlyByteBuf buf, IWaystone waystone) {
        buf.writeUUID(waystone.getWaystoneUid());
        buf.writeResourceLocation(waystone.getWaystoneType());
        buf.writeUtf(waystone.getName());
        buf.writeBoolean(waystone.isGlobal());
        buf.writeResourceLocation(waystone.getDimension().location());
        buf.writeBlockPos(waystone.getPos());
    }

    public static CompoundTag write(IWaystone waystone, CompoundTag compound) {
        compound.put("WaystoneUid", NbtUtils.createUUID(waystone.getWaystoneUid()));
        compound.putString("Type", waystone.getWaystoneType().toString());
        compound.putString("Name", waystone.getName());
        compound.putString("World", waystone.getDimension().location().toString());
        compound.put("BlockPos", NbtUtils.writeBlockPos(waystone.getPos()));
        compound.putBoolean("WasGenerated", waystone.wasGenerated());
        if (waystone.getOwnerUid() != null) {
            compound.put("OwnerUid", NbtUtils.createUUID(waystone.getOwnerUid()));
        }
        compound.putBoolean("IsGlobal", waystone.isGlobal());
        return compound;
    }
}
