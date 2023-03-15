package net.blay09.mods.waystones.menu;

import net.blay09.mods.waystones.block.entity.WarpPlateBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;

public class WarpPlateAttunementSlot extends Slot {
    private final WarpPlateBlockEntity warpPlate;

    public WarpPlateAttunementSlot(WarpPlateBlockEntity container, int slot, int x, int y) {
        super(container, slot, x, y);
        warpPlate = container;
    }

    @Override
    public boolean mayPickup(Player player) {
        return warpPlate.isCompletedFirstAttunement() && super.mayPickup(player);
    }
}
