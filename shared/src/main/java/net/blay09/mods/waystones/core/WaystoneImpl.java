package net.blay09.mods.waystones.core;

import com.google.common.collect.Lists;
import net.blay09.mods.waystones.api.*;
import net.blay09.mods.waystones.api.WaystoneTypes;
import net.blay09.mods.waystones.block.WaystoneBlock;
import net.blay09.mods.waystones.tag.ModBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WaystoneImpl implements Waystone, MutableWaystone {

    private final ResourceLocation waystoneType;
    private final UUID waystoneUid;
    private final WaystoneOrigin origin;

    private ResourceKey<Level> dimension;
    private BlockPos pos;

    private String name = "";
    private WaystoneVisibility visibility;

    private UUID ownerUid;

    public WaystoneImpl(ResourceLocation waystoneType, UUID waystoneUid, ResourceKey<Level> dimension, BlockPos pos, WaystoneOrigin origin, @Nullable UUID ownerUid) {
        this.waystoneType = waystoneType;
        this.waystoneUid = waystoneUid;
        this.dimension = dimension;
        this.pos = pos;
        this.origin = origin;
        this.ownerUid = ownerUid;

        if (WaystoneTypes.isSharestone(waystoneType)) {
            this.visibility = WaystoneVisibility.GLOBAL;
        } else if (waystoneType.equals(WaystoneTypes.WARP_PLATE)) {
            this.visibility = WaystoneVisibility.SHARD_ONLY;
        } else if (waystoneType.equals(WaystoneTypes.LANDING_STONE)) {
            this.visibility = WaystoneVisibility.SHARD_ONLY;
        } else {
            this.visibility = WaystoneVisibility.ACTIVATION;
        }
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
    public WaystoneOrigin getOrigin() {
        return origin;
    }

    @Override
    public WaystoneVisibility getVisibility() {
        return visibility;
    }

    @Override
    public void setVisibility(WaystoneVisibility visibility) {
        this.visibility = visibility;
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

    @Override
    public boolean isValidInLevel(ServerLevel level) {
        BlockState state = level.getBlockState(pos);
        return state.is(ModBlockTags.IS_TELEPORT_TARGET);
    }

    @Override
    public TeleportDestination resolveDestination(Level level) {
        BlockState state = level.getBlockState(pos);
        Direction direction = state.hasProperty(WaystoneBlock.FACING) ? state.getValue(WaystoneBlock.FACING) : Direction.NORTH;
        // Use a list to keep order intact - it might check one direction twice, but no one cares
        List<Direction> directionCandidates = Lists.newArrayList(direction, Direction.EAST, Direction.WEST, Direction.SOUTH, Direction.NORTH);
        for (Direction candidate : directionCandidates) {
            BlockPos offsetPos = pos.relative(candidate);
            BlockPos offsetPosUp = offsetPos.above();
            if (level.getBlockState(offsetPos).isSuffocating(level, offsetPos) || level.getBlockState(offsetPosUp).isSuffocating(level, offsetPosUp)) {
                continue;
            }

            direction = candidate;
            break;
        }

        BlockPos targetPos = (getWaystoneType().equals(WaystoneTypes.WARP_PLATE) || getWaystoneType().equals(WaystoneTypes.LANDING_STONE)) ? getPos() : getPos().relative(
                direction);
        Vec3 location = new Vec3(targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5);
        return new TeleportDestination(level, location, direction);
    }

    public static List<Waystone> readList(FriendlyByteBuf buf) {
        int size = buf.readShort();
        List<Waystone> waystones = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            waystones.add(read(buf));
        }
        return waystones;
    }

    public static Waystone read(FriendlyByteBuf buf) {
        UUID waystoneUid = buf.readUUID();
        ResourceLocation waystoneType = buf.readResourceLocation();
        String name = buf.readUtf();
        final var visibility = buf.readEnum(WaystoneVisibility.class);
        ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(buf.readUtf(250)));
        BlockPos pos = buf.readBlockPos();
        WaystoneOrigin origin = buf.readEnum(WaystoneOrigin.class);

        WaystoneImpl waystone = new WaystoneImpl(waystoneType, waystoneUid, dimension, pos, origin, null);
        waystone.setName(name);
        waystone.setVisibility(visibility);
        return waystone;
    }

    public static Waystone read(CompoundTag compound) {
        UUID waystoneUid = NbtUtils.loadUUID(Objects.requireNonNull(compound.get("WaystoneUid")));
        String name = compound.getString("Name");
        ResourceKey<Level> dimensionType = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(compound.getString("World")));
        BlockPos pos = NbtUtils.readBlockPos(compound.getCompound("BlockPos"));
        boolean wasGenerated = compound.getBoolean("WasGenerated"); // legacy
        WaystoneOrigin origin = wasGenerated ? WaystoneOrigin.WILDERNESS : WaystoneOrigin.UNKNOWN;
        if (compound.contains("Origin")) {
            try {
                origin = WaystoneOrigin.valueOf(compound.getString("Origin"));
            } catch (IllegalArgumentException ignored) {
            }
        }
        UUID ownerUid = compound.contains("OwnerUid") ? NbtUtils.loadUUID(Objects.requireNonNull(compound.get("OwnerUid"))) : null;
        ResourceLocation waystoneType = compound.contains("Type") ? new ResourceLocation(compound.getString("Type")) : WaystoneTypes.WAYSTONE;
        WaystoneImpl waystone = new WaystoneImpl(waystoneType, waystoneUid, dimensionType, pos, origin, ownerUid);
        waystone.setName(name);
        if (compound.contains("Visibility")) {
            waystone.setVisibility(WaystoneVisibility.valueOf(compound.getString("Visibility")));
        } else {
            waystone.setVisibility(compound.getBoolean("IsGlobal") ? WaystoneVisibility.GLOBAL : WaystoneVisibility.ACTIVATION);
        }
        return waystone;
    }

    public static void writeList(FriendlyByteBuf buf, Collection<Waystone> waystones) {
        buf.writeShort(waystones.size());
        for (Waystone waystone : waystones) {
            write(buf, waystone);
        }
    }

    public static void write(FriendlyByteBuf buf, Waystone waystone) {
        buf.writeUUID(waystone.getWaystoneUid());
        buf.writeResourceLocation(waystone.getWaystoneType());
        buf.writeUtf(waystone.getName());
        buf.writeEnum(waystone.getVisibility());
        buf.writeResourceLocation(waystone.getDimension().location());
        buf.writeBlockPos(waystone.getPos());
        buf.writeEnum(waystone.getOrigin());
    }

    public static CompoundTag write(Waystone waystone, CompoundTag compound) {
        compound.put("WaystoneUid", NbtUtils.createUUID(waystone.getWaystoneUid()));
        compound.putString("Type", waystone.getWaystoneType().toString());
        compound.putString("Name", waystone.getName());
        compound.putString("World", waystone.getDimension().location().toString());
        compound.put("BlockPos", NbtUtils.writeBlockPos(waystone.getPos()));
        compound.putString("Origin", waystone.getOrigin().name());
        if (waystone.getOwnerUid() != null) {
            compound.put("OwnerUid", NbtUtils.createUUID(waystone.getOwnerUid()));
        }
        compound.putString("Visibility", waystone.getVisibility().name());
        return compound;
    }
}
