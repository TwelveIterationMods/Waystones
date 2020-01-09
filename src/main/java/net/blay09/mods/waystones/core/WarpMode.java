package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.WaystoneConfig;

import java.util.function.Supplier;

public enum WarpMode {
	INVENTORY_BUTTON(WaystoneConfig.COMMON.inventoryButtonXpCost::get),
	WARP_SCROLL(() -> false),
	RETURN_SCROLL(() -> false),
	BOUND_SCROLL(() -> false),
	WARP_STONE(WaystoneConfig.COMMON.warpStoneXpCost::get),
	WAYSTONE_TO_WAYSTONE(WaystoneConfig.COMMON.waystoneXpCost::get);

	public static WarpMode[] values = values();

    private final Supplier<Boolean> xpCostSupplier;

	WarpMode(Supplier<Boolean> xpCostSupplier) {
		this.xpCostSupplier = xpCostSupplier;
	}

	public boolean hasXpCost() {
		return xpCostSupplier.get();
	}
}
