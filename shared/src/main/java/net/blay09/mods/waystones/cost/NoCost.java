package net.blay09.mods.waystones.cost;

import net.blay09.mods.waystones.api.cost.Cost;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class NoCost implements Cost {
    public static final Cost INSTANCE = new NoCost();

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
    public int getNumericalCost(Player player) {
        return 0;
    }

    @Override
    public void appendHoverText(Player player, List<Component> tooltip) {
    }

}
