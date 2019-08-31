package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.PlayerWaystoneData;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageSortWaystone {

    private final int index;
    private final int otherIndex;

    public MessageSortWaystone(int index, int otherIndex) {
        this.index = index;
        this.otherIndex = otherIndex;
    }

    public static void encode(MessageSortWaystone message, PacketBuffer buf) {
        buf.writeByte(message.index);
        buf.writeByte(message.otherIndex);
    }

    public static MessageSortWaystone decode(PacketBuffer buf) {
        int index = buf.readByte();
        int otherIndex = buf.readByte();
        return new MessageSortWaystone(index, otherIndex);
    }

    public static void handle(MessageSortWaystone message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayerEntity player = context.getSender();
            if (player == null) {
                return;
            }

            PlayerWaystoneData waystoneData = PlayerWaystoneData.fromPlayer(player);
            WaystoneEntry[] entries = waystoneData.getWaystones();
            int index = message.index;
            int otherIndex = message.otherIndex;
            if (index < 0 || index >= entries.length || otherIndex < 0 || otherIndex >= entries.length) {
                return;
            }
            WaystoneEntry swap = entries[index];
            entries[index] = entries[otherIndex];
            entries[otherIndex] = swap;
            waystoneData.store(player);
            WaystoneManager.sendPlayerWaystones(player);
        });
    }

}
