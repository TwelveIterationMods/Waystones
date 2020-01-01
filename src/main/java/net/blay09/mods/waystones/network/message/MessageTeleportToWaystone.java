package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.core.WarpMode;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WaystoneProxy;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class MessageTeleportToWaystone {

    private final IWaystone waystone;
    private final WarpMode warpMode;
    private final Hand hand;
    private final IWaystone fromWaystone;

    public MessageTeleportToWaystone(IWaystone waystone, WarpMode warpMode, Hand hand, @Nullable IWaystone fromWaystone) {
        this.waystone = waystone;
        this.warpMode = warpMode;
        this.hand = hand;
        this.fromWaystone = fromWaystone;
    }

    public static void encode(MessageTeleportToWaystone message, PacketBuffer buf) {
        buf.writeUniqueId(message.waystone.getWaystoneUid());
        buf.writeByte(message.warpMode.ordinal());
        if (message.warpMode == WarpMode.WARP_SCROLL || message.warpMode == WarpMode.WARP_STONE) {
            buf.writeByte(message.hand.ordinal());
        } else if (message.warpMode == WarpMode.WAYSTONE) {
            buf.writeUniqueId(message.fromWaystone.getWaystoneUid());
        }
    }

    public static MessageTeleportToWaystone decode(PacketBuffer buf) {
        IWaystone waystone = new WaystoneProxy(buf.readUniqueId());
        WarpMode warpMode = WarpMode.values()[buf.readByte()];
        Hand hand = (warpMode == WarpMode.WARP_SCROLL || warpMode == WarpMode.WARP_STONE) ? Hand.values()[buf.readByte()] : Hand.MAIN_HAND;
        IWaystone fromWaystone = warpMode == WarpMode.WAYSTONE ? new WaystoneProxy(buf.readUniqueId()) : null;
        return new MessageTeleportToWaystone(waystone, warpMode, hand, fromWaystone);
    }

    public static void handle(MessageTeleportToWaystone message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayerEntity player = context.getSender();
            if (player == null) {
                return;
            }

            ItemStack heldItem = player.getHeldItem(message.hand);
            PlayerWaystoneManager.tryTeleportToWaystone(player, message.waystone, message.warpMode, heldItem, message.fromWaystone);
        });
        context.setPacketHandled(true);
    }


}
