package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.config.WaystoneConfig;

import java.util.function.Supplier;

public enum WarpMode {
    INVENTORY_BUTTON(WaystoneConfig.SERVER.inventoryButtonXpCostMultiplier::get),
    WARP_SCROLL(() -> 0.0),
    RETURN_SCROLL(() -> 0.0),
    BOUND_SCROLL(() -> 0.0),
    WARP_STONE(WaystoneConfig.SERVER.warpStoneXpCostMultiplier::get),
    WAYSTONE_TO_WAYSTONE(WaystoneConfig.SERVER.waystoneXpCostMultiplier::get);

    public static WarpMode[] values = values();

    private final Supplier<Double> xpCostMultiplierSupplier;

    WarpMode(Supplier<Double> xpCostMultiplierSupplier) {
        this.xpCostMultiplierSupplier = xpCostMultiplierSupplier;
    }

    public double getXpCostMultiplier() {
        return xpCostMultiplierSupplier.get();
    }
}
