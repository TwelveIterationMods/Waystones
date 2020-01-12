package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.config.WaystoneConfig;

import java.util.function.Supplier;

public enum WarpMode {
    INVENTORY_BUTTON(WaystoneConfig.SERVER.inventoryButtonXpCostMultiplier::get, false),
    WARP_SCROLL(() -> 0.0, true),
    RETURN_SCROLL(() -> 0.0, true),
    BOUND_SCROLL(() -> 0.0, true),
    WARP_STONE(WaystoneConfig.SERVER.warpStoneXpCostMultiplier::get, false),
    WAYSTONE_TO_WAYSTONE(WaystoneConfig.SERVER.waystoneXpCostMultiplier::get, false);

    public static WarpMode[] values = values();

    private final Supplier<Double> xpCostMultiplierSupplier;
    private final boolean consumesItem;

    WarpMode(Supplier<Double> xpCostMultiplierSupplier, boolean consumesItem) {
        this.xpCostMultiplierSupplier = xpCostMultiplierSupplier;
        this.consumesItem = consumesItem;
    }

    public double getXpCostMultiplier() {
        return xpCostMultiplierSupplier.get();
    }

    public boolean consumesItem() {
        return consumesItem;
    }
}
