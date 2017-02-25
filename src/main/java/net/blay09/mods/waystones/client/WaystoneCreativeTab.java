package net.blay09.mods.waystones.client;

import net.blay09.mods.waystones.Waystones;
import net.minecraft.item.ItemStack;

public class WaystoneCreativeTab extends net.minecraft.creativetab.CreativeTabs {

	private static ItemStack itemStack;

	public WaystoneCreativeTab() {
		super("Waystone");
	}

	@Override
	public ItemStack getTabIconItem() {
		if (itemStack == null) {
			itemStack = new ItemStack(Waystones.blockWaystone);
		}
		return itemStack;
	}
}
