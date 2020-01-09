package net.blay09.mods.waystones.container;

import net.blay09.mods.waystones.api.IWaystone;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class WaystoneSelectionContainer extends Container {

    private final IWaystone fromWaystone;

    public WaystoneSelectionContainer(int windowId, @Nullable IWaystone fromWaystone) {
        super(ModContainers.waystoneSelection, windowId);
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
}
