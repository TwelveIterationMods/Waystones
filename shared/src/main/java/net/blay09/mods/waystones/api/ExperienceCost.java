package net.blay09.mods.waystones.api;

import net.blay09.mods.waystones.xp.ExperienceLevelCost;
import net.blay09.mods.waystones.xp.ExperiencePointsCost;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public interface ExperienceCost {
    boolean canAfford(Player player);

    void consume(Player player);

    int getCostAsLevels(Player player);

    void appendHoverText(Player player, List<Component> tooltip);

    boolean isEmpty();

    static ExperienceCost fromLevels(int levels) {
        return new ExperienceLevelCost(levels);
    }

    static ExperienceCost fromExperience(int experience) {
        return new ExperiencePointsCost(experience);
    }

    class NoExperienceCost implements ExperienceCost {
        public static final ExperienceCost INSTANCE = new NoExperienceCost();

        @Override
        public boolean canAfford(Player player) {
            return true;
        }

        @Override
        public void consume(Player player) {
        }

        @Override
        public int getCostAsLevels(Player player) {
            return 0;
        }

        @Override
        public void appendHoverText(Player player, List<Component> tooltip) {
            tooltip.add(Component.translatable("gui.waystones.waystone_selection.no_xp_requirement"));
        }

        @Override
        public boolean isEmpty() {
            return true;
        }
    }
}
