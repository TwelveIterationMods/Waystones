package net.blay09.mods.waystones.comparator;

import net.blay09.mods.waystones.api.IWaystone;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class UserSortingComparator implements Comparator<IWaystone> {

    private final List<UUID> sortingIndex;

    public UserSortingComparator(List<UUID> sortingIndex) {
        this.sortingIndex = sortingIndex;
    }

    @Override
    public int compare(IWaystone o1, IWaystone o2) {
        final var index1 = sortingIndex.indexOf(o1.getWaystoneUid());
        final var index2 = sortingIndex.indexOf(o2.getWaystoneUid());
        if (index1 == -1 && index2 == -1) {
            return 0;
        } else if (index1 == -1) {
            return 1;
        } else if (index2 == -1) {
            return -1;
        }

        return Integer.compare(index1, index2);
    }
}
