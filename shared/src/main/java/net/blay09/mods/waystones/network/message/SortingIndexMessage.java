package net.blay09.mods.waystones.network.message;

import net.blay09.mods.balm.api.BalmEnvironment;
import net.blay09.mods.waystones.core.InMemoryPlayerWaystoneData;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SortingIndexMessage {

    private final List<UUID> sortingIndex;

    public SortingIndexMessage(List<UUID> sortingIndex) {
        this.sortingIndex = sortingIndex;
    }

    public static void encode(SortingIndexMessage message, FriendlyByteBuf buf) {
        buf.writeShort(message.sortingIndex.size());
        for (final var waystoneUid : message.sortingIndex) {
            buf.writeUUID(waystoneUid);
        }
    }

    public static SortingIndexMessage decode(FriendlyByteBuf buf) {
        final int count = buf.readShort();
        final var sortingIndex = new ArrayList<UUID>();
        for (int i = 0; i < count; i++) {
            sortingIndex.add(buf.readUUID());
        }
        return new SortingIndexMessage(sortingIndex);
    }

    public static void handle(Player player, SortingIndexMessage message) {
        final var playerWaystoneData = (InMemoryPlayerWaystoneData) PlayerWaystoneManager.getPlayerWaystoneData(BalmEnvironment.CLIENT);
        playerWaystoneData.setSortingIndex(player, message.sortingIndex);
    }
}
