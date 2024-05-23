package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class WaystoneVisibilities {
    public static List<WaystoneVisibility> getVisibilityOptions(Player player, Waystone waystone) {
        final var result = new ArrayList<WaystoneVisibility>();
        final var baseVisibility = WaystoneVisibility.fromWaystoneType(waystone.getWaystoneType());
        result.add(baseVisibility);
        if (baseVisibility == WaystoneVisibility.ACTIVATION) {
            if (WaystonePermissionManager.isAllowedVisibility(WaystoneVisibility.GLOBAL) || WaystonePermissionManager.skipsPermissions(player)) {
                result.add(WaystoneVisibility.GLOBAL);
            }
        }
        return result;
    }
}
