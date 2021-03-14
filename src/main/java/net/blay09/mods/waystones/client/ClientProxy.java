package net.blay09.mods.waystones.client;

import net.blay09.mods.waystones.CommonProxy;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;

import java.util.Locale;

public class ClientProxy extends CommonProxy {

    private boolean isVivecraftInstalled;

    public ClientProxy() {
        isVivecraftInstalled = ClientBrandRetriever.getClientModName().toLowerCase(Locale.ENGLISH).contains("vivecraft");
    }

    @Override
    public void playSound(SoundEvent sound, BlockPos pos, float pitch) {
        Minecraft.getInstance().getSoundHandler().play(new SimpleSound(sound, SoundCategory.BLOCKS, WaystonesConfig.CLIENT.soundVolume.get().floatValue(), pitch, pos));
    }

    @Override
    public void setWaystoneCooldowns(long inventoryButtonCooldownUntil, long warpStoneCooldownUntil) {
        PlayerWaystoneManager.setInventoryButtonCooldownUntil(Minecraft.getInstance().player, inventoryButtonCooldownUntil);
        PlayerWaystoneManager.setWarpStoneCooldownUntil(Minecraft.getInstance().player, warpStoneCooldownUntil);
    }

    @Override
    public boolean isVivecraftInstalled() {
        return isVivecraftInstalled;
    }
}
