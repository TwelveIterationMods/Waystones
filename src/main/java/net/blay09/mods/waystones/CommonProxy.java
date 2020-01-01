package net.blay09.mods.waystones;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.core.WarpMode;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class CommonProxy {

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
