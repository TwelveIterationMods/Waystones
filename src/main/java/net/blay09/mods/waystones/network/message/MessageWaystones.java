package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.PlayerWaystoneHelper;
import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.client.ClientWaystones;
import net.blay09.mods.waystones.item.WarpStoneItem;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageWaystones {

    private final WaystoneEntry[] entries;
    private final long lastFreeWarp;
    private final long lastWarpStoneUse;

    public MessageWaystones(WaystoneEntry[] entries, long lastFreeWarp, long lastWarpStoneUse) {
        this.entries = entries;
        this.lastFreeWarp = lastFreeWarp;
        this.lastWarpStoneUse = lastWarpStoneUse;
    }

    public static void encode(MessageWaystones message, PacketBuffer buf) {
        buf.writeShort(message.entries.length);
        for (WaystoneEntry entry : message.entries) {
            entry.write(buf);
        }
        buf.writeLong(message.lastFreeWarp);
        buf.writeLong(Math.max(0, WaystoneConfig.SERVER.warpStoneCooldown.get() * 1000 - (System.currentTimeMillis() - message.lastWarpStoneUse)));
    }

    public static MessageWaystones decode(PacketBuffer buf) {
        WaystoneEntry[] entries = new WaystoneEntry[buf.readShort()];
        for (int i = 0; i < entries.length; i++) {
            entries[i] = WaystoneEntry.read(buf);
        }
        long lastFreeWarp = buf.readLong();
        long lastWarpStoneUse = buf.readLong();
        return new MessageWaystones(entries, lastFreeWarp, lastWarpStoneUse);
    }

    public static void handle(MessageWaystones message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ClientWaystones.setKnownWaystones(message.entries);
            PlayerWaystoneHelper.store(Minecraft.getInstance().player, message.entries, message.lastFreeWarp, message.lastWarpStoneUse);
            WarpStoneItem.lastTimerUpdate = System.currentTimeMillis();
        });
        context.setPacketHandled(true);
    }
}
