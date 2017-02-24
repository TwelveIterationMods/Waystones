package net.blay09.mods.waystones.client;

import net.blay09.mods.waystones.Waystones;
import net.minecraft.item.ItemStack;

public class WaystoneCreativeTab extends net.minecraft.creativetab.CreativeTabs {

    private static ItemStack itemStack;

    public WaystoneCreativeTab() {
        super("Waystone");
        itemStack = new ItemStack(Waystones.blockWaystone);
    }

    @Override
    public ItemStack getTabIconItem() {
        return itemStack;
    }
}
