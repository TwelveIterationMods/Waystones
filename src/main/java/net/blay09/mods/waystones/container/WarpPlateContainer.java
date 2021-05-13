package net.blay09.mods.waystones.container;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.tileentity.WarpPlateTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class WarpPlateContainer extends Container {

    private final WarpPlateTileEntity tileEntity;

    public WarpPlateContainer(int windowId, WarpPlateTileEntity tileEntity, PlayerInventory playerInventory) {
        super(ModContainers.warpPlate, windowId);
        this.tileEntity = tileEntity;

        ItemStackHandler itemStackHandler = tileEntity.getItemStackHandler();
        addSlot(new SlotItemHandler(itemStackHandler, 0, 80, 45));
        addSlot(new SlotItemHandler(itemStackHandler, 1, 80, 17));
        addSlot(new SlotItemHandler(itemStackHandler, 2, 108, 45));
        addSlot(new SlotItemHandler(itemStackHandler, 3, 80, 73));
        addSlot(new SlotItemHandler(itemStackHandler, 4, 52, 45));

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 104 + i * 18));
            }
        }

        for (int j = 0; j < 9; ++j) {
            addSlot(new Slot(playerInventory, j, 8 + j * 18, 162));
        }
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        BlockPos pos = tileEntity.getPos();
        return player.getDistanceSq((double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5) <= 64;
    }

    public float getAttunementProgress() {
        return tileEntity.getWorld().getGameTime() % 20 / 20f;
    }
}
