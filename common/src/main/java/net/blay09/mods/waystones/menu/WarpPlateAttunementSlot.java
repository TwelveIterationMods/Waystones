package net.blay09.mods.waystones.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class WarpPlateAttunementSlot extends Slot {
    public WarpPlateAttunementSlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Override
    public boolean mayPickup(Player player) {
        return container.canTakeItem(container, getContainerSlot(), getItem());
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        if (this.getContainerSlot() == 0) {
            return 1;
        }
        return stack.getMaxStackSize();
    }

}
