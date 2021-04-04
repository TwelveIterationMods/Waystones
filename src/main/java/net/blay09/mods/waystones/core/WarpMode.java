package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.config.WaystonesConfig;

import java.util.function.Supplier;

public enum WarpMode {
    INVENTORY_BUTTON(WaystonesConfig.SERVER.inventoryButtonXpCostMultiplier::get, false),
    WARP_SCROLL(() -> 0.0, true),
    RETURN_SCROLL(() -> 0.0, true),
    BOUND_SCROLL(() -> 0.0, true),
    WARP_STONE(WaystonesConfig.SERVER.warpStoneXpCostMultiplier::get, false),
    WAYSTONE_TO_WAYSTONE(WaystonesConfig.SERVER.waystoneXpCostMultiplier::get, false),
    SHARESTONE_TO_SHARESTONE(WaystonesConfig.SERVER.sharestoneXpCostMultiplier::get, false),
    WARP_PLATE(WaystonesConfig.SERVER.warpPlateXpCostMultiplier::get, false);

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
