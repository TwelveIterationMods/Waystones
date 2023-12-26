package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneOrigin;
import net.blay09.mods.waystones.api.error.WaystoneEditError;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.Set;

public class WaystonePermissionManager {

    private static final Set<WaystoneVisibility> DEFAULT_VISIBILITIES = Set.of(WaystoneVisibility.SHARD_ONLY, WaystoneVisibility.ACTIVATION);

    public static Optional<WaystoneEditError> mayEditWaystone(Player player, Level world, Waystone waystone) {
        if (skipsPermissions(player)) {
            return Optional.empty();
        }

        final var config = WaystonesConfig.getActive();
        if (waystone.hasOwner() && config.general.restrictEdits.contains(WaystoneOrigin.PLAYER) && !waystone.isOwner(player)) {
            return Optional.of(new WaystoneEditError.NotOwner());
        }

        if (waystone.getOrigin() != WaystoneOrigin.PLAYER && config.general.restrictEdits.contains(waystone.getOrigin())) {
            return Optional.of(new WaystoneEditError.NotOwner());
        }

        if (!isAllowedVisibility(waystone.getVisibility())) {
            return Optional.of(new WaystoneEditError.RequiresCreative());
        }

        return Optional.empty();
    }

    public static boolean isAllowedVisibility(WaystoneVisibility visibility) {
        final var config = WaystonesConfig.getActive();
        return DEFAULT_VISIBILITIES.contains(visibility) || config.general.allowedVisibilities.contains(visibility);
    }

    public static boolean skipsPermissions(Player player) {
        return player.getAbilities().instabuild;
    }
}
