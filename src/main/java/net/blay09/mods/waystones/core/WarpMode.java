package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.minecraft.entity.player.PlayerEntity;

import java.util.function.BiPredicate;
import java.util.function.Supplier;

public enum WarpMode {
    INVENTORY_BUTTON(WaystonesConfig.SERVER.inventoryButtonXpCostMultiplier::get, WarpMode::waystoneIsActivatedOrNamed, false),
    WARP_SCROLL(() -> 0.0, WarpMode::waystoneIsActivated, true),
    RETURN_SCROLL(() -> 0.0, WarpMode::waystoneIsActivated, true),
    BOUND_SCROLL(() -> 0.0, WarpMode::always, true),
    WARP_STONE(WaystonesConfig.SERVER.warpStoneXpCostMultiplier::get, WarpMode::waystoneIsActivated, false),
    WAYSTONE_TO_WAYSTONE(WaystonesConfig.SERVER.waystoneXpCostMultiplier::get, WarpMode::waystoneIsActivated, false),
    SHARESTONE_TO_SHARESTONE(WaystonesConfig.SERVER.sharestoneXpCostMultiplier::get, WarpMode::sharestonesOnly, false),
    WARP_PLATE(WaystonesConfig.SERVER.warpPlateXpCostMultiplier::get, WarpMode::warpPlatesOnly, false);

    public static WarpMode[] values = values();

    private final Supplier<Double> xpCostMultiplierSupplier;
    private final BiPredicate<PlayerEntity, IWaystone> allowTeleportPredicate;
    private final boolean consumesItem;

    WarpMode(Supplier<Double> xpCostMultiplierSupplier, BiPredicate<PlayerEntity, IWaystone> allowTeleportPredicate, boolean consumesItem) {
        this.xpCostMultiplierSupplier = xpCostMultiplierSupplier;
        this.allowTeleportPredicate = allowTeleportPredicate;
        this.consumesItem = consumesItem;
    }

    public double getXpCostMultiplier() {
        return xpCostMultiplierSupplier.get();
    }

    public boolean consumesItem() {
        return consumesItem;
    }

    private static boolean always(PlayerEntity player, IWaystone waystone) {
        return true;
    }

    private static boolean waystoneIsActivatedOrNamed(PlayerEntity player, IWaystone waystone) {
        return WaystonesConfig.getInventoryButtonMode().hasNamedTarget() || (waystone.getWaystoneType().equals(WaystoneTypes.WAYSTONE) && PlayerWaystoneManager.isWaystoneActivated(player, waystone));
    }

    private static boolean waystoneIsActivated(PlayerEntity player, IWaystone waystone) {
        return waystone.getWaystoneType().equals(WaystoneTypes.WAYSTONE) && PlayerWaystoneManager.isWaystoneActivated(player, waystone);
    }

    private static boolean sharestonesOnly(PlayerEntity player, IWaystone waystone) {
        return waystone.getWaystoneType().equals(WaystoneTypes.SHARESTONE);
    }

    private static boolean warpPlatesOnly(PlayerEntity player, IWaystone waystone) {
        return waystone.getWaystoneType().equals(WaystoneTypes.WARP_PLATE);
    }

    public BiPredicate<PlayerEntity, IWaystone> getAllowTeleportPredicate() {
        return allowTeleportPredicate;
    }
}
