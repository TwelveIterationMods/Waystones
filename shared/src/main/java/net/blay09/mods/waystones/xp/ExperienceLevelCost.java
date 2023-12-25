package net.blay09.mods.waystones.xp;

import net.blay09.mods.waystones.api.cost.Cost;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class ExperienceLevelCost implements Cost {
    private int levels;

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
    public void rollback(Player player) {
        player.giveExperienceLevels(levels);
    }

    @Override
    public int getNumericalCost(Player player) {
        return levels;
    }

    @Override
    public boolean isEmpty() {
        return levels == 0;
    }

    @Override
    public void appendHoverText(Player player, List<Component> tooltip) {
        tooltip.add(Component.translatable("gui.waystones.waystone_selection.level_requirement", levels));
    }

    public void setLevels(int levels) {
        this.levels = levels;
    }

    public int getLevels() {
        return levels;
    }
}
