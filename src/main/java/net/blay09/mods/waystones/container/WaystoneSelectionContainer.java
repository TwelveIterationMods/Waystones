package net.blay09.mods.waystones.container;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.core.WarpMode;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class WaystoneSelectionContainer extends Container {

    private final WarpMode warpMode;
    private final IWaystone fromWaystone;

    public WaystoneSelectionContainer(ContainerType<WaystoneSelectionContainer> type, WarpMode warpMode, @Nullable IWaystone fromWaystone, int windowId) {
        super(type, windowId);
        this.warpMode = warpMode;
        this.fromWaystone = fromWaystone;
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        if (fromWaystone != null) {
            BlockPos pos = fromWaystone.getPos();
            return player.getDistanceSq((double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5) <= 64;
        }

        return true;
    }

    @Nullable
    public IWaystone getWaystoneFrom() {
        return fromWaystone;
    }

    public WarpMode getWarpMode() {
        return warpMode;
    }
}
