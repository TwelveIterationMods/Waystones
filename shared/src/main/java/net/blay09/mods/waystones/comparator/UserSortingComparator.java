package net.blay09.mods.waystones.comparator;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.minecraft.world.entity.player.Player;

import java.util.Comparator;

public class UserSortingComparator implements Comparator<IWaystone> {

    private final Player player;

    public UserSortingComparator(Player player) {
        this.player = player;
    }

    @Override
    public int compare(IWaystone o1, IWaystone o2) {
        final var settings1 = PlayerWaystoneManager.getUserSettingsForWaystone(player, o1);
        final var settings2 = PlayerWaystoneManager.getUserSettingsForWaystone(player, o2);
        final var sortIndex1 = settings1.sortIndex();
        final var sortIndex2 = settings2.sortIndex();
        return Integer.compare(sortIndex1, sortIndex2);
    }
}
