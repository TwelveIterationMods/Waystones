package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SortWaystoneMessage {

    private final int index;
    private final int otherIndex;

    public SortWaystoneMessage(int index, int otherIndex) {
        this.index = index;
        this.otherIndex = otherIndex;
    }

    public static void encode(SortWaystoneMessage message, PacketBuffer buf) {
        buf.writeByte(message.index);
        buf.writeByte(message.otherIndex);
    }

    public static SortWaystoneMessage decode(PacketBuffer buf) {
        int index = buf.readByte();
        int otherIndex = buf.readByte();
        return new SortWaystoneMessage(index, otherIndex);
    }

    public static void handle(SortWaystoneMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayerEntity player = context.getSender();
            if (player == null) {
                return;
            }

            PlayerWaystoneManager.swapWaystoneSorting(player, message.index, message.otherIndex);
        });
        context.setPacketHandled(true);
    }

}
