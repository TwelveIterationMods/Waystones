package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.Waystones;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PlayerWaystoneCooldownsMessage {

    private final long inventoryButtonCooldownUntil;
    private final long warpStoneCooldownUntil;

    public PlayerWaystoneCooldownsMessage(long inventoryButtonCooldownUntil, long warpStoneCooldownUntil) {
        this.inventoryButtonCooldownUntil = inventoryButtonCooldownUntil;
        this.warpStoneCooldownUntil = warpStoneCooldownUntil;
    }

    public static void encode(PlayerWaystoneCooldownsMessage message, PacketBuffer buf) {
        buf.writeLong(message.inventoryButtonCooldownUntil);
        buf.writeLong(message.warpStoneCooldownUntil);
    }

    public static PlayerWaystoneCooldownsMessage decode(PacketBuffer buf) {
        long inventoryButtonCooldownUntil = buf.readLong();
        long warpStoneCooldownUntil = buf.readLong();
        return new PlayerWaystoneCooldownsMessage(inventoryButtonCooldownUntil, warpStoneCooldownUntil);
    }

    public static void handle(PlayerWaystoneCooldownsMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Waystones.proxy.setWaystoneCooldowns(message.inventoryButtonCooldownUntil, message.warpStoneCooldownUntil);
        });
        context.setPacketHandled(true);
    }
}
