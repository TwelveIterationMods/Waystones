package net.blay09.mods.waystones.requirement;

import net.blay09.mods.waystones.api.requirement.WarpRequirement;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class NoRequirement implements WarpRequirement {
    public static final WarpRequirement INSTANCE = new NoRequirement();

    @Override
    public boolean canAfford(Player player) {
        return true;
    }

    @Override
    public void consume(Player player) {
    }

    @Override
    public void rollback(Player player) {
    }

    @Override
    public int getNumericalValue(Player player) {
        return 0;
    }

    @Override
    public void appendHoverText(Player player, List<Component> tooltip) {
    }

}
