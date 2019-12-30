package net.blay09.mods.waystones;

import com.google.common.collect.Lists;
import net.blay09.mods.waystones.core.IWaystone;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.List;

public class CommonProxy {

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        GlobalWaystones globalWaystones = GlobalWaystones.get(event.getPlayer().world);
        PlayerWaystoneData waystoneData = PlayerWaystoneData.fromPlayer(event.getPlayer());
        List<IWaystone> validWaystones = Lists.newArrayList();
        for (IWaystone waystone : waystoneData.getWaystones()) {
            if (waystone.isGlobal()) {
                if (globalWaystones.getGlobalWaystone(waystone.getName()) == null) {
                    continue;
                }
            }
            validWaystones.add(waystone);
        }
        for (IWaystone waystone : globalWaystones.getGlobalWaystones()) {
            if (!validWaystones.contains(waystone)) {
                validWaystones.add(waystone);
            }
        }
        PlayerWaystoneHelper.store(event.getPlayer(), validWaystones.toArray(new IWaystone[0]), waystoneData.getLastFreeWarp(), waystoneData.getLastWarpStoneUse());

        WaystoneManagerLegacy.sendPlayerWaystones(event.getPlayer());
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        WaystoneManagerLegacy.sendPlayerWaystones(event.getPlayer());
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        WaystoneManagerLegacy.sendPlayerWaystones(event.getPlayer());
    }

    public void openWaystoneSelection(PlayerEntity player, WarpMode mode, Hand hand, @Nullable IWaystone fromWaystone) {

    }

    public void openWaystoneSettings(PlayerEntity player, IWaystone waystone, boolean fromSelectionGui) {

    }

    public void playSound(SoundEvent soundEvent, BlockPos pos, float pitch) {

    }

    public boolean isVivecraftInstalled() {
        return false;
    }
}
