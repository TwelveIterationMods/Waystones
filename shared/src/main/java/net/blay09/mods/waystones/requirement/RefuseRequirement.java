package net.blay09.mods.waystones.requirement;

import net.blay09.mods.waystones.api.requirement.WarpRequirement;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class RefuseRequirement implements WarpRequirement {

    private Component message = Component.empty();

    public RefuseRequirement() {
    }

    public RefuseRequirement(Component message) {
        this.message = message;
    }

    @Override
    public boolean canAfford(Player player) {
        return false;
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
        tooltip.add(message);
    }

    public void setMessage(Component message) {
        this.message = message;
    }

    public Component getMessage() {
        return message;
    }
}
