package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.IWaystone;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public class WaystoneProxy implements IWaystone {

    private final UUID waystoneUid;
    private IWaystone backingWaystone;

    public WaystoneProxy(UUID waystoneUid) {
        this.waystoneUid = waystoneUid;
    }

    @Override
    public boolean isValid() {
        return WaystoneManager.get().getWaystoneById(waystoneUid).isPresent();
    }

    public IWaystone getBackingWaystone() {
        if (backingWaystone == null) {
            backingWaystone = WaystoneManager.get().getWaystoneById(waystoneUid).orElse(InvalidWaystone.INSTANCE);
        }

        return backingWaystone;
    }

    @Override
    public UUID getOwnerUid() {
        return getBackingWaystone().getOwnerUid();
    }

    @Override
    public UUID getWaystoneUid() {
        return waystoneUid;
    }

    @Override
    public String getName() {
        return getBackingWaystone().getName();
    }

    @Override
    public RegistryKey<World> getDimension() {
        return getBackingWaystone().getDimension();
    }

    @Override
    public boolean wasGenerated() {
        return getBackingWaystone().wasGenerated();
    }

    @Override
    public boolean isGlobal() {
        return getBackingWaystone().isGlobal();
    }

    @Override
    public boolean isOwner(PlayerEntity player) {
        return getBackingWaystone().isOwner(player);
    }

    @Override
    public BlockPos getPos() {
        return getBackingWaystone().getPos();
    }

    @Override
    public ResourceLocation getWaystoneType() {
        return getBackingWaystone().getWaystoneType();
    }
}
