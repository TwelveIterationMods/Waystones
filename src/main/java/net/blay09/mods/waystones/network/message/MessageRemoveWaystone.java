package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.PlayerWaystoneData;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageRemoveWaystone {

    private final int index;

    public MessageRemoveWaystone(int index) {
        this.index = index;
    }

    public static void encode(MessageRemoveWaystone message, PacketBuffer buf) {
        buf.writeByte(message.index);
    }

    public static MessageRemoveWaystone decode(PacketBuffer buf) {
        int index = buf.readByte();
        return new MessageRemoveWaystone(index);
    }

    public static void handle(MessageRemoveWaystone message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayerEntity player = context.getSender();
            if (player == null) {
                return;
            }

            PlayerWaystoneData waystoneData = PlayerWaystoneData.fromPlayer(player);
            WaystoneEntry[] entries = waystoneData.getWaystones();
            int index = message.index;
            if (index < 0 || index >= entries.length) {
                return;
            }

            WaystoneManager.removePlayerWaystone(player, entries[index]);
            WaystoneManager.sendPlayerWaystones(player);
        });
        context.setPacketHandled(true);
    }

}
