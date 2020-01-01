package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.PlayerWaystoneHelper;
import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.item.WarpStoneItem;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageWaystones {

    private final IWaystone[] entries;
    private final long lastFreeWarp;
    private final long lastWarpStoneUse;

    public MessageWaystones(IWaystone[] entries, long lastFreeWarp, long lastWarpStoneUse) {
        this.entries = entries;
        this.lastFreeWarp = lastFreeWarp;
        this.lastWarpStoneUse = lastWarpStoneUse;
    }

    public static void encode(MessageWaystones message, PacketBuffer buf) {
        buf.writeShort(message.entries.length);
        for (IWaystone entry : message.entries) {
            // TODO entry.write(buf);
        }
        buf.writeLong(message.lastFreeWarp);
        buf.writeLong(Math.max(0, WaystoneConfig.SERVER.warpStoneCooldown.get() * 1000 - (System.currentTimeMillis() - message.lastWarpStoneUse)));
    }

    public static MessageWaystones decode(PacketBuffer buf) {
        IWaystone[] entries = new IWaystone[buf.readShort()];
        for (int i = 0; i < entries.length; i++) {
            // TODO entries[i] = WaystoneEntry.read(buf);
        }
        long lastFreeWarp = buf.readLong();
        long lastWarpStoneUse = buf.readLong();
        return new MessageWaystones(entries, lastFreeWarp, lastWarpStoneUse);
    }

    public static void handle(MessageWaystones message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            // TODO ClientWaystones.setKnownWaystones(message.entries);
            PlayerWaystoneHelper.store(Minecraft.getInstance().player, message.entries, message.lastFreeWarp, message.lastWarpStoneUse);
            WarpStoneItem.lastTimerUpdate = System.currentTimeMillis();
        });
        context.setPacketHandled(true);
    }
}
