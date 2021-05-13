package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.IWaystone;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

public class InvalidWaystone implements IWaystone {

    public static final IWaystone INSTANCE = new InvalidWaystone();

    @Override
    public boolean isValid() {
        return false;
    }

    @Nullable
    @Override
    public UUID getOwnerUid() {
        return null;
    }

    @Override
    public UUID getWaystoneUid() {
        return UUID.randomUUID();
    }

    @Override
    public String getName() {
        return "invalid";
    }

    @Override
    public RegistryKey<World> getDimension() {
        return World.OVERWORLD;
    }

    @Override
    public boolean wasGenerated() {
        return false;
    }

    @Override
    public boolean isGlobal() {
        return false;
    }

    @Override
    public boolean isOwner(PlayerEntity player) {
        return false;
    }

    @Override
    public BlockPos getPos() {
        return BlockPos.ZERO;
    }

    @Nullable
    @Override
    public IWaystone getTargetWaystone() {
        return null;
    }

    @Override
    public ResourceLocation getWaystoneType() {
        return new ResourceLocation(Waystones.MOD_ID, "invalid");
    }
}
