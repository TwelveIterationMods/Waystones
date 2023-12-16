package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class WaystonePermissionManager {

    public static boolean mayBreakWaystone(Player player, BlockGetter world, BlockPos pos) {
        if (WaystonesConfig.getActive().restrictions.restrictToCreative && !player.getAbilities().instabuild) {
            return false;
        }

        return WaystoneManager.get(player.getServer()).getWaystoneAt(world, pos).map(waystone -> {

            if (!player.getAbilities().instabuild) {
                if (waystone.wasGenerated() && WaystonesConfig.getActive().restrictions.generatedWaystonesUnbreakable) {
                    return false;
                }

                boolean isGlobal = waystone.isGlobal();
                boolean mayBreakGlobalWaystones = !WaystonesConfig.getActive().restrictions.globalWaystoneSetupRequiresCreativeMode;
                return !isGlobal || mayBreakGlobalWaystones;
            }


            return true;
        }).orElse(true);

    }

    public static boolean mayPlaceWaystone(@Nullable Player player) {
        return !WaystonesConfig.getActive().restrictions.restrictToCreative || (player != null && player.getAbilities().instabuild);
    }

    public static WaystoneEditPermissions mayEditWaystone(Player player, Level world, IWaystone waystone) {
        if (WaystonesConfig.getActive().restrictions.restrictToCreative && !player.getAbilities().instabuild) {
            return WaystoneEditPermissions.NOT_CREATIVE;
        }

        if (WaystonesConfig.getActive().restrictions.restrictRenameToOwner && !waystone.isOwner(player)) {
            return WaystoneEditPermissions.NOT_THE_OWNER;
        }

        if (waystone.isGlobal() && !player.getAbilities().instabuild && WaystonesConfig.getActive().restrictions.globalWaystoneSetupRequiresCreativeMode) {
            return WaystoneEditPermissions.GET_CREATIVE;
        }

        return WaystoneEditPermissions.ALLOW;
    }
}
