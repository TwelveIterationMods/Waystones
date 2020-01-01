package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WaystoneProxy;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageRemoveWaystone {

    private final IWaystone waystone;

    public MessageRemoveWaystone(IWaystone waystone) {
        this.waystone = waystone;
    }

    public static void encode(MessageRemoveWaystone message, PacketBuffer buf) {
        buf.writeUniqueId(message.waystone.getWaystoneUid());
    }

    public static MessageRemoveWaystone decode(PacketBuffer buf) {
        IWaystone waystone = new WaystoneProxy(buf.readUniqueId());
        return new MessageRemoveWaystone(waystone);
    }

    public static void handle(MessageRemoveWaystone message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayerEntity player = context.getSender();
            if (player == null) {
                return;
            }

            PlayerWaystoneManager.deactivateWaystone(player, message.waystone);
        });
        context.setPacketHandled(true);
    }

}
