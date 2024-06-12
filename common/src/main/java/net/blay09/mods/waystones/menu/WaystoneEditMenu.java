package net.blay09.mods.waystones.menu;

import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.blay09.mods.waystones.core.WaystoneImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class WaystoneEditMenu extends AbstractContainerMenu {

    public record Data(BlockPos pos, Waystone waystone, boolean canEdit) {
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, WaystoneEditMenu.Data> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC.cast(),
            WaystoneEditMenu.Data::pos,
            WaystoneImpl.STREAM_CODEC,
            WaystoneEditMenu.Data::waystone,
            ByteBufCodecs.BOOL,
            WaystoneEditMenu.Data::canEdit,
            WaystoneEditMenu.Data::new);

    private final Waystone waystone;
    private final WaystoneBlockEntityBase blockEntity;
    private final boolean canEdit;

    public WaystoneEditMenu(int windowId, Waystone waystone, WaystoneBlockEntityBase blockEntity, Inventory playerInventory, boolean canEdit) {
        super(ModMenus.waystoneSettings.get(), windowId);
        this.waystone = waystone;
        this.blockEntity = blockEntity;
        this.canEdit = canEdit;
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

    public Waystone getWaystone() {
        return waystone;
    }

    public boolean canEdit() {
        return canEdit;
    }
}
