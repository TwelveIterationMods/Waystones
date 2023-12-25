package net.blay09.mods.waystones.cost;

import net.blay09.mods.waystones.api.cost.Cost;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WaystoneSyncManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class CooldownCost implements Cost {

    private ResourceLocation key;
    private int seconds;

    public CooldownCost(ResourceLocation key, int seconds) {
        this.key = key;
        this.seconds = seconds;
    }

    @Override
    public boolean canAfford(Player player) {
        return PlayerWaystoneManager.getCooldownMillisLeft(player, key) <= 0;
    }

    @Override
    public void consume(Player player) {
        if (seconds > 0) {
            PlayerWaystoneManager.setCooldownUntil(player, key, System.currentTimeMillis() + seconds * 1000L);
            WaystoneSyncManager.sendWaystoneCooldowns(player);
        }
    }

    @Override
    public void rollback(Player player) {
        PlayerWaystoneManager.setCooldownUntil(player, key, 0);
    }

    @Override
    public int getNumericalCost(Player player) {
        return 0;
    }

    @Override
    public void appendHoverText(Player player, List<Component> tooltip) {
        final var millisLeft = PlayerWaystoneManager.getCooldownMillisLeft(player, key);
        if (millisLeft > 0) {
            tooltip.add(Component.translatable("tooltip.waystones.cooldown_left", millisLeft / 1000).withStyle(ChatFormatting.GOLD));
        }
    }

    public void setCooldown(ResourceLocation key, int seconds) {
        this.key = key;
        this.seconds = seconds;
    }

    public int getCooldownSeconds() {
        return seconds;
    }
}
