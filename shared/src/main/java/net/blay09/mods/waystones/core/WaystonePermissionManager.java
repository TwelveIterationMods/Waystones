package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class WaystonePermissionManager {

    public static WaystoneEditPermissions mayEditWaystone(Player player, Level world, IWaystone waystone) {
        if (WaystonesConfig.getActive().restrictions.restrictRenameToOwner && !waystone.isOwner(player)) {
            return WaystoneEditPermissions.NOT_THE_OWNER;
        }

        if (waystone.getVisibility() == WaystoneVisibility.GLOBAL && !player.getAbilities().instabuild && WaystonesConfig.getActive().restrictions.globalWaystoneSetupRequiresCreativeMode) {
            return WaystoneEditPermissions.GET_CREATIVE;
        }

        return WaystoneEditPermissions.ALLOW;
    }
}
