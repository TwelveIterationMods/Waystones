package net.blay09.mods.waystones.api.cost;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public interface Cost {
    boolean canAfford(Player player);

    void consume(Player player);

    void rollback(Player player);

    int getNumericalCost(Player player);

    void appendHoverText(Player player, List<Component> tooltip);

}
