package net.blay09.mods.waystones.menu;

import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class WarpPlateAttunementSlot extends Slot {
    private final WaystoneBlockEntityBase blockEntity;

    public WarpPlateAttunementSlot(WaystoneBlockEntityBase blockEntity, int slot, int x, int y) {
        super(blockEntity, slot, x, y);
        this.blockEntity = blockEntity;
    }

    @Override
    public boolean mayPickup(Player player) {
        return blockEntity.isCompletedFirstAttunement() && super.mayPickup(player);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        if (this.getContainerSlot() == 0) {
            return 1;
        }
        return stack.getMaxStackSize();
    }

}
