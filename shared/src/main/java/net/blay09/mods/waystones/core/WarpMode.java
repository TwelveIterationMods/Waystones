package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.IWaystoneTeleportContext;
import net.blay09.mods.waystones.config.WaystonesConfig;

import java.util.function.Supplier;

/**
 * @deprecated Use {@link IWaystoneTeleportContext#consumesWarpItem()} instead. XP cost multipliers will be refactored out soon. WarpMode will be removed in 1.20.4.
 */
@Deprecated(forRemoval = true)
public enum WarpMode {
    INVENTORY_BUTTON(() -> WaystonesConfig.getActive().costs.inventoryButtonXpCostMultiplier,  false),
    WARP_SCROLL(() -> 0.0, true),
    RETURN_SCROLL(() -> 0.0,  true),
    WARP_STONE(() -> WaystonesConfig.getActive().costs.warpStoneXpCostMultiplier,  false),
    WAYSTONE_TO_WAYSTONE(() -> WaystonesConfig.getActive().costs.waystoneXpCostMultiplier,  false),
    SHARESTONE_TO_SHARESTONE(() -> WaystonesConfig.getActive().costs.sharestoneXpCostMultiplier,  false),
    WARP_PLATE(() -> WaystonesConfig.getActive().costs.warpPlateXpCostMultiplier, false),
    PORTSTONE_TO_WAYSTONE(() -> WaystonesConfig.getActive().costs.portstoneXpCostMultiplier,  false),
    CUSTOM(() -> 0.0, false);

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

    /**
     * @deprecated Item consumption is now controlled through the {@link IWaystoneTeleportContext}.
     */
    @Deprecated(forRemoval = true)
    public boolean consumesItem() {
        return consumesItem;
    }

}
