package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.WarpMode;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.core.IWaystone;
import net.blay09.mods.waystones.core.WaystoneProxy;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageOpenWaystoneSelection {
    private final WarpMode warpMode;
    private final Hand hand;
    private final IWaystone waystone;

    public MessageOpenWaystoneSelection(WarpMode warpMode, Hand hand, IWaystone waystone) {
        this.warpMode = warpMode;
        this.hand = hand;
        this.waystone = waystone;
    }


    public static void encode(MessageOpenWaystoneSelection message, PacketBuffer buf) {
        buf.writeByte(message.warpMode.ordinal());
        buf.writeByte(message.hand.ordinal());
        buf.writeUniqueId(message.waystone.getWaystoneUid());
    }

    public static MessageOpenWaystoneSelection decode(PacketBuffer buf) {
        WarpMode warpMode = WarpMode.values()[buf.readByte()];
        Hand hand = Hand.values()[buf.readByte()];
        IWaystone waystone = new WaystoneProxy(buf.readUniqueId());
        return new MessageOpenWaystoneSelection(warpMode, hand, waystone);
    }

    public static void handle(MessageOpenWaystoneSelection message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayerEntity player = context.getSender();
            if (player == null) {
                return;
            }

            Waystones.proxy.openWaystoneSelection(player, message.warpMode, message.hand, message.waystone);
        });
        context.setPacketHandled(true);
    }
}
