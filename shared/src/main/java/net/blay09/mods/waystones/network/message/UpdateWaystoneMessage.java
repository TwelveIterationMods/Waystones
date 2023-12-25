package net.blay09.mods.waystones.network.message;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.event.WaystoneUpdateReceivedEvent;
import net.blay09.mods.waystones.core.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public class UpdateWaystoneMessage {

    private final Waystone waystone;

    public UpdateWaystoneMessage(Waystone waystone) {
        this.waystone = waystone;
    }

    public static void encode(UpdateWaystoneMessage message, FriendlyByteBuf buf) {
        WaystoneImpl.write(buf, message.waystone);
    }

    public static UpdateWaystoneMessage decode(FriendlyByteBuf buf) {
        return new UpdateWaystoneMessage(WaystoneImpl.read(buf));
    }

    public static void handle(Player player, UpdateWaystoneMessage message) {
        WaystoneManagerImpl.get(player.getServer()).updateWaystone(message.waystone);
        Balm.getEvents().fireEvent(new WaystoneUpdateReceivedEvent(message.waystone));
    }
}
