package net.blay09.mods.waystones.container;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WarpMode;
import net.blay09.mods.waystones.core.WaystoneManager;
import net.blay09.mods.waystones.core.WaystoneTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.List;

public class WaystoneSelectionContainer extends Container {

    private final WarpMode warpMode;
    private final IWaystone fromWaystone;
    private final List<IWaystone> waystones;

    public WaystoneSelectionContainer(ContainerType<WaystoneSelectionContainer> type, WarpMode warpMode, @Nullable IWaystone fromWaystone, int windowId, List<IWaystone> waystones) {
        super(type, windowId);
        this.warpMode = warpMode;
        this.fromWaystone = fromWaystone;
        this.waystones = waystones;
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

    public List<IWaystone> getWaystones() {
        return waystones;
    }

    public static WaystoneSelectionContainer createWaystoneSelection(int windowId, PlayerEntity player, WarpMode warpMode, @Nullable IWaystone fromWaystone) {
        List<IWaystone> waystones = PlayerWaystoneManager.getWaystones(player);
        return new WaystoneSelectionContainer(ModContainers.waystoneSelection, warpMode, fromWaystone, windowId, waystones);
    }

    public static WaystoneSelectionContainer createSharestoneSelection(int windowId, IWaystone fromWaystone) {
        List<IWaystone> waystones = WaystoneManager.get().getWaystonesByType(WaystoneTypes.SHARESTONE);
        return new WaystoneSelectionContainer(ModContainers.sharestoneSelection, WarpMode.SHARESTONE_TO_SHARESTONE, fromWaystone, windowId, waystones);
    }
}
