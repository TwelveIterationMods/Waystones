package net.blay09.mods.waystones.network.message;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.WaystoneRemoveReceivedEvent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class WaystoneRemovedMessage {

    private final ResourceLocation waystoneType;
    private final UUID waystoneId;
    private final boolean wasDestroyed;

    public WaystoneRemovedMessage(ResourceLocation waystoneType, UUID waystoneId, boolean wasDestroyed) {
        this.waystoneType = waystoneType;
        this.waystoneId = waystoneId;
        this.wasDestroyed = wasDestroyed;
    }

    public static void encode(WaystoneRemovedMessage message, FriendlyByteBuf buf) {
        buf.writeResourceLocation(message.waystoneType);
        buf.writeUUID(message.waystoneId);
        buf.writeBoolean(message.wasDestroyed);
    }

    public static WaystoneRemovedMessage decode(FriendlyByteBuf buf) {
        return new WaystoneRemovedMessage(buf.readResourceLocation(), buf.readUUID(), buf.readBoolean());
    }

    public static void handle(Player player, WaystoneRemovedMessage message) {
        Balm.getEvents().fireEvent(new WaystoneRemoveReceivedEvent(message.waystoneType, message.waystoneId, message.wasDestroyed));
    }
}
