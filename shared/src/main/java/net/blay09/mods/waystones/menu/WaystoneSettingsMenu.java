package net.blay09.mods.waystones.menu;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.api.WaystoneTypes;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class WaystoneSettingsMenu extends AbstractContainerMenu {

    private final IWaystone waystone;
    private final WaystoneBlockEntityBase blockEntity;
    private final ContainerData containerData;

    public WaystoneSettingsMenu(int windowId, IWaystone waystone, WaystoneBlockEntityBase blockEntity, ContainerData containerData, Inventory playerInventory) {
        super(ModMenus.waystoneSettings.get(), windowId);
        this.waystone = waystone;
        this.blockEntity = blockEntity;
        this.containerData = containerData;

        checkContainerDataCount(containerData, 1);

        addSlot(new Slot(blockEntity, 0, 80, 48));
        addSlot(new Slot(blockEntity, 1, 52, 48));
        addSlot(new Slot(blockEntity, 2, 66, 69));
        addSlot(new Slot(blockEntity, 3, 94, 69));
        addSlot(new Slot(blockEntity, 4, 108, 48));

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 104 + i * 18));
            }
        }

        for (int j = 0; j < 9; ++j) {
            addSlot(new Slot(playerInventory, j, 8 + j * 18, 162));
        }

        addDataSlots(containerData);
    }

    public float getAttunementProgress() {
        return containerData.get(0) / (float) blockEntity.getMaxAttunementTicks();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemStack = slotStack.copy();
            if (index < 5) {
                if (!this.moveItemStackTo(slotStack, 5, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!getSlot(0).hasItem()) {
                    if (!this.moveItemStackTo(slotStack.split(1), 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!this.moveItemStackTo(slotStack, 1, 5, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemStack;
    }

    @Override
    public boolean stillValid(Player player) {
        BlockPos pos = blockEntity.getBlockPos();
        return player.distanceToSqr((double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5) <= 64;
    }

    public IWaystone getWaystone() {
        return waystone;
    }

    public List<WaystoneVisibility> getVisibilityOptions() {
        if (WaystoneTypes.isSharestone(waystone.getWaystoneType())) {
            return List.of(WaystoneVisibility.GLOBAL);
        } else if(waystone.getWaystoneType().equals(WaystoneTypes.WARP_PLATE)) {
            return List.of(WaystoneVisibility.SHARD_ONLY);
        } else if(waystone.getWaystoneType().equals(WaystoneTypes.LANDING_STONE)) {
            return List.of(WaystoneVisibility.SHARD_ONLY);
        } else {
            return List.of(WaystoneVisibility.ACTIVATION, WaystoneVisibility.GLOBAL);
        }
    }
}
