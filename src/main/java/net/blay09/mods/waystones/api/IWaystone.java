package net.blay09.mods.waystones.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

public interface IWaystone {
    UUID getWaystoneUid();

    String getName();

    RegistryKey<World> getDimension();

    boolean wasGenerated();

    boolean isGlobal();

    boolean isOwner(PlayerEntity player);

    BlockPos getPos();

    boolean isValid();

    @Nullable
    UUID getOwnerUid();

    @Nullable
    IWaystone getTargetWaystone();

    ResourceLocation getWaystoneType();
}
