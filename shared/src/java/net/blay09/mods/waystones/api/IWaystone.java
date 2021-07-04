package net.blay09.mods.waystones.api;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface IWaystone {
    UUID getWaystoneUid();

    String getName();

    ResourceKey<Level> getDimension();

    boolean wasGenerated();

    boolean isGlobal();

    boolean isOwner(Player player);

    BlockPos getPos();

    boolean isValid();

    @Nullable
    UUID getOwnerUid();

    ResourceLocation getWaystoneType();

    default boolean hasName() {
        return !getName().isEmpty();
    }

    default boolean hasOwner() {
        return getOwnerUid() != null;
    }
}
