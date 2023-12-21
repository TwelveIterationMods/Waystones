package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.api.WaystoneEditError;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class WaystonePermissionManager {

    public static Optional<WaystoneEditError> mayEditWaystone(Player player, Level world, IWaystone waystone) {
        if (WaystonesConfig.getActive().restrictions.restrictEditToOwner && !waystone.isOwner(player)) {
            return Optional.of(new WaystoneEditError.NotOwner());
        }

        if (waystone.getVisibility() == WaystoneVisibility.GLOBAL && !mayEditGlobalWaystones(player)) {
            return Optional.of(new WaystoneEditError.RequiresCreative());
        }

        return Optional.empty();
    }

    public static boolean mayEditGlobalWaystones(Player player) {
        return player.getAbilities().instabuild || !WaystonesConfig.getActive().restrictions.globalWaystoneSetupRequiresCreativeMode;
    }
}
