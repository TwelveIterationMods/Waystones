package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.IWaystone;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

import javax.annotation.Nullable;
import java.util.UUID;

public class Waystone implements IWaystone {

    private final UUID waystoneUid;
    private final String name;
    private final DimensionType dimensionType;
    private final BlockPos pos;
    private final boolean wasGenerated;

    private boolean isGlobal;
    private UUID ownerUid;

    public Waystone(UUID waystoneUid, String name, DimensionType dimensionType, BlockPos pos, boolean wasGenerated, @Nullable UUID ownerUid) {
        this.waystoneUid = waystoneUid;
        this.name = name;
        this.dimensionType = dimensionType;
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
    public DimensionType getDimensionType() {
        return dimensionType;
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
    public boolean isOwner(PlayerEntity player) {
        return ownerUid == null || player.getGameProfile().getId().equals(ownerUid) || player.abilities.isCreativeMode;
    }

    @Override
    public BlockPos getPos() {
        return pos;
    }

    @Override
    public boolean isValid() {
        return true; // TODO actually check it though
    }

    @Override
    public UUID getOwnerUid() {
        return ownerUid;
    }
}
