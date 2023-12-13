package net.blay09.mods.waystones.xp;

import net.blay09.mods.waystones.api.ExperienceCost;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class ExperienceLevelCost implements ExperienceCost {
    private final int levels;

    public ExperienceLevelCost(int levels) {
        this.levels = Math.max(0, levels);
    }

    @Override
    public boolean canAfford(Player player) {
        return player.experienceLevel >= levels;
    }

    @Override
    public void consume(Player player) {
        player.giveExperienceLevels(-levels);
    }

    @Override
    public int getCostAsLevels(Player player) {
        return levels;
    }

    @Override
    public boolean isEmpty() {
        return levels == 0;
    }

    @Override
    public Component getCostAsTooltip(Player player) {
        return Component.translatable("gui.waystones.waystone_selection.level_requirement", getCostAsLevels(player));
    }
}
