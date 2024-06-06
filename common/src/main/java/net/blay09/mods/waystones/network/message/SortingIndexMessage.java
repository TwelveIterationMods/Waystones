package net.blay09.mods.waystones.network.message;

import net.blay09.mods.balm.api.BalmEnvironment;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.core.InMemoryPlayerWaystoneData;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SortingIndexMessage implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SortingIndexMessage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Waystones.MOD_ID,
            "sorting_index"));

    private final List<UUID> sortingIndex;

    public SortingIndexMessage(List<UUID> sortingIndex) {
        this.sortingIndex = sortingIndex;
    }

    public static void encode(FriendlyByteBuf buf, SortingIndexMessage message) {
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

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
