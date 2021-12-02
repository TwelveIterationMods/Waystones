package net.blay09.mods.waystones.menu;

import net.blay09.mods.waystones.api.IWaystone;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class WaystoneSettingsMenu extends AbstractContainerMenu {

    private final IWaystone waystone;

    public WaystoneSettingsMenu(MenuType<WaystoneSettingsMenu> type, IWaystone waystone, int windowId) {
        super(type, windowId);
        this.waystone = waystone;
    }

    @Override
    public boolean stillValid(Player player) {
        BlockPos pos = waystone.getPos();
        return player.distanceToSqr((double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5) <= 64;
    }

    public IWaystone getWaystone() {
        return waystone;
    }
}
