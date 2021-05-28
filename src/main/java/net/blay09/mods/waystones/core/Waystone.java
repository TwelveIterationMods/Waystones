package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.IMutableWaystone;
import net.blay09.mods.waystones.api.IWaystone;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

public class Waystone implements IWaystone, IMutableWaystone {

    private final ResourceLocation waystoneType;
    private final UUID waystoneUid;
    private final boolean wasGenerated;

    private RegistryKey<World> dimension;
    private BlockPos pos;

    private String name = "";
    private boolean isGlobal;

    private UUID ownerUid;

    public Waystone(ResourceLocation waystoneType, UUID waystoneUid, RegistryKey<World> dimension, BlockPos pos, boolean wasGenerated, @Nullable UUID ownerUid) {
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
    public RegistryKey<World> getDimension() {
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
    public boolean isOwner(PlayerEntity player) {
        return ownerUid == null || player.getGameProfile().getId().equals(ownerUid) || player.abilities.isCreativeMode;
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

    public void setDimension(RegistryKey<World> dimension) {
        this.dimension = dimension;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public ResourceLocation getWaystoneType() {
        return waystoneType;
    }

    public static IWaystone read(PacketBuffer buf) {
        UUID waystoneUid = buf.readUniqueId();
        ResourceLocation waystoneType = buf.readResourceLocation();
        String name = buf.readString();
        boolean isGlobal = buf.readBoolean();
        RegistryKey<World> dimension = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(buf.readString(250)));
        BlockPos pos = buf.readBlockPos();

        Waystone waystone = new Waystone(waystoneType, waystoneUid, dimension, pos, false, null);
        waystone.setName(name);
        waystone.setGlobal(isGlobal);
        return waystone;
    }

    public static IWaystone read(CompoundNBT compound) {
        UUID waystoneUid = NBTUtil.readUniqueId(Objects.requireNonNull(compound.get("WaystoneUid")));
        String name = compound.getString("Name");
        RegistryKey<World> dimensionType = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(compound.getString("World")));
        BlockPos pos = NBTUtil.readBlockPos(compound.getCompound("BlockPos"));
        boolean wasGenerated = compound.getBoolean("WasGenerated");
        UUID ownerUid = compound.contains("OwnerUid") ? NBTUtil.readUniqueId(Objects.requireNonNull(compound.get("OwnerUid"))) : null;
        ResourceLocation waystoneType = compound.contains("Type") ? new ResourceLocation(compound.getString("Type")) : WaystoneTypes.WAYSTONE;
        Waystone waystone = new Waystone(waystoneType, waystoneUid, dimensionType, pos, wasGenerated, ownerUid);
        waystone.setName(name);
        waystone.setGlobal(compound.getBoolean("IsGlobal"));
        return waystone;
    }

    public static void write(PacketBuffer buf, IWaystone waystone) {
        buf.writeUniqueId(waystone.getWaystoneUid());
        buf.writeResourceLocation(waystone.getWaystoneType());
        buf.writeString(waystone.getName());
        buf.writeBoolean(waystone.isGlobal());
        buf.writeResourceLocation(waystone.getDimension().getLocation());
        buf.writeBlockPos(waystone.getPos());
    }

    public static CompoundNBT write(IWaystone waystone, CompoundNBT compound) {
        compound.put("WaystoneUid", NBTUtil.func_240626_a_(waystone.getWaystoneUid())); // writeUniqueId
        compound.putString("Type", waystone.getWaystoneType().toString());
        compound.putString("Name", waystone.getName());
        compound.putString("World", waystone.getDimension().getLocation().toString());
        compound.put("BlockPos", NBTUtil.writeBlockPos(waystone.getPos()));
        compound.putBoolean("WasGenerated", waystone.wasGenerated());
        if (waystone.getOwnerUid() != null) {
            compound.put("OwnerUid", NBTUtil.func_240626_a_(waystone.getOwnerUid())); // writeUniqueId
        }
        compound.putBoolean("IsGlobal", waystone.isGlobal());
        return compound;
    }
}
