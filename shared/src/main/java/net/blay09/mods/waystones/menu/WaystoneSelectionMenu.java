package net.blay09.mods.waystones.menu;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.core.WarpMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class WaystoneSelectionMenu extends AbstractContainerMenu {

    private final WarpMode warpMode;
    private final IWaystone fromWaystone;
    private final Collection<IWaystone> waystones;

    public WaystoneSelectionMenu(MenuType<WaystoneSelectionMenu> type, WarpMode warpMode, @Nullable IWaystone fromWaystone, int windowId, Collection<IWaystone> waystones) {
        super(type, windowId);
        this.warpMode = warpMode;
        this.fromWaystone = fromWaystone;
        this.waystones = waystones;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        if (fromWaystone != null) {
            BlockPos pos = fromWaystone.getPos();
            return player.distanceToSqr((double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5) <= 64;
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

    public Collection<IWaystone> getWaystones() {
        return waystones;
    }

}
