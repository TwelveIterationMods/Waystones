package net.blay09.mods.waystones.requirement;

import net.blay09.mods.waystones.api.requirement.WarpRequirement;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;
import java.util.List;

public class CombinedRequirement implements WarpRequirement {

    private final Collection<WarpRequirement> warpRequirements;

    public CombinedRequirement(Collection<WarpRequirement> warpRequirements) {
        this.warpRequirements = warpRequirements;
    }

    @Override
    public boolean canAfford(Player player) {
        return warpRequirements.stream().allMatch(requirement -> requirement.canAfford(player));
    }

    @Override
    public void consume(Player player) {
        warpRequirements.forEach(requirement -> requirement.consume(player));
    }

    @Override
    public void rollback(Player player) {
        warpRequirements.forEach(requirement -> requirement.rollback(player));
    }

    @Override
    public int getNumericalValue(Player player) {
        return warpRequirements.stream().mapToInt(requirement -> requirement.getNumericalValue(player)).max().orElse(0);
    }

    @Override
    public void appendHoverText(Player player, List<Component> tooltip) {
        warpRequirements.forEach(requirement -> requirement.appendHoverText(player, tooltip));
    }

}
