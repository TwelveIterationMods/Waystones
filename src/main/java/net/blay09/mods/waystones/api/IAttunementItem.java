package net.blay09.mods.waystones.api;

import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public interface IAttunementItem {
    @Nullable
    IWaystone getWaystoneAttunedTo(ItemStack itemStack);
}
