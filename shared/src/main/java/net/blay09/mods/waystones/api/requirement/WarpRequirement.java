package net.blay09.mods.waystones.api.requirement;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public interface WarpRequirement {
    boolean canAfford(Player player);

    void consume(Player player);

    void rollback(Player player);

    /**
     * Right now, should return the cost in terms of "units", be it xp, levels, or items. Used for the level cost display. Only supports 0-3, above shows as 3+.
     *
     * @deprecated In the future, this will be replaced by proper control over how a requirement should be displayed.
     */
    @Deprecated
    int getNumericalValue(Player player);

    void appendHoverText(Player player, List<Component> tooltip);

}
