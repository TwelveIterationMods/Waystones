package net.blay09.mods.waystones.requirement;

import net.blay09.mods.waystones.api.requirement.WarpRequirement;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ItemRequirement implements WarpRequirement {

    private ItemStack itemStack;
    private int count;

    public ItemRequirement(ItemStack item, int count) {
        this.itemStack = item;
        this.count = count;
    }

    @Override
    public boolean canAfford(Player player) {
        int count = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            final var slotStack = player.getInventory().getItem(i);
            if (ItemStack.isSameItemSameTags(itemStack, slotStack)) {
                count += slotStack.getCount();

                if (count >= this.count) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void consume(Player player) {
        var consumed = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            final var leftToConsume = this.count - consumed;
            final var slotStack = player.getInventory().getItem(i);
            if (ItemStack.isSameItemSameTags(itemStack, slotStack)) {
                final var count = Math.min(slotStack.getCount(), leftToConsume);
                slotStack.shrink(count);
                consumed += count;

                if (consumed >= this.count) {
                    return;
                }
            }
        }
    }

    @Override
    public void rollback(Player player) {
        var added = 0;
        while(added < count) {
            final var leftToAdd = count - added;
            final var itemStack = this.itemStack.copy();
            itemStack.setCount(Math.min(itemStack.getMaxStackSize(), leftToAdd));
            if (!player.addItem(itemStack)) {
                player.drop(itemStack, false, false);
            }
            added += itemStack.getCount();
        }
    }

    @Override
    public int getNumericalValue(Player player) {
        return count;
    }

    @Override
    public void appendHoverText(Player player, List<Component> tooltip) {
        if (count > 0) {
            tooltip.add(Component.translatable("gui.waystones.waystone_selection.item_requirement", count, itemStack.getHoverName()).withStyle(ChatFormatting.LIGHT_PURPLE));
        }
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
