package net.blay09.mods.waystones.cost;

import net.blay09.mods.waystones.api.cost.Cost;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;
import java.util.List;

public class CombinedCost implements Cost {

    private final Collection<Cost> costs;

    public CombinedCost(Collection<Cost> costs) {
        this.costs = costs;
    }

    @Override
    public boolean canAfford(Player player) {
        return costs.stream().allMatch(cost -> cost.canAfford(player));
    }

    @Override
    public void consume(Player player) {
        costs.forEach(cost -> cost.consume(player));
    }

    @Override
    public void rollback(Player player) {
        costs.forEach(cost -> cost.rollback(player));
    }

    @Override
    public int getNumericalCost(Player player) {
        return costs.stream().mapToInt(cost -> cost.getNumericalCost(player)).max().orElse(0);
    }

    @Override
    public void appendHoverText(Player player, List<Component> tooltip) {
        costs.forEach(cost -> cost.appendHoverText(player, tooltip));
    }

    @Override
    public boolean isEmpty() {
        return costs.stream().allMatch(Cost::isEmpty);
    }
}
