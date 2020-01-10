package net.blay09.mods.waystones.container;

import net.blay09.mods.waystones.api.IWaystone;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.math.BlockPos;

public class WaystoneSettingsContainer extends Container {

    private final IWaystone waystone;

    public WaystoneSettingsContainer(int windowId, IWaystone waystone) {
        super(ModContainers.waystoneSettings, windowId);
        this.waystone = waystone;
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        BlockPos pos = waystone.getPos();
        return player.getDistanceSq((double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5) <= 64;
    }

    public IWaystone getWaystone() {
        return waystone;
    }
}
