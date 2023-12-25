package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.WaystonesAPI;
import net.blay09.mods.waystones.core.WaystoneTeleportManager;
import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.blay09.mods.waystones.core.WaystoneProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class SelectWaystoneMessage {

    private final UUID waystoneUid;

    public SelectWaystoneMessage(UUID waystoneUid) {
        this.waystoneUid = waystoneUid;
    }

    public static void encode(SelectWaystoneMessage message, FriendlyByteBuf buf) {
        buf.writeUUID(message.waystoneUid);
    }

    public static SelectWaystoneMessage decode(FriendlyByteBuf buf) {
        final var waystoneUid = buf.readUUID();
        return new SelectWaystoneMessage(waystoneUid);
    }

    public static void handle(ServerPlayer player, SelectWaystoneMessage message) {
        if (!(player.containerMenu instanceof WaystoneSelectionMenu selectionMenu)) {
            return;
        }

        final var waystone = new WaystoneProxy(player.server, message.waystoneUid);
        if (selectionMenu.getWaystones().stream().noneMatch(it -> it.getWaystoneUid().equals(waystone.getWaystoneUid()))) {
            Waystones.logger.warn("{} tried to teleport to waystone {} that they don't have access to.",
                    player.getName().getString(),
                    waystone.getWaystoneUid());
            return;
        }

        WaystonesAPI.createDefaultTeleportContext(player, waystone, it -> {
                    it.setFromWaystone(selectionMenu.getWaystoneFrom());
                    it.addFlags(selectionMenu.getFlags());
                })
                .ifLeft(WaystonesAPI::tryTeleport);
        player.closeContainer();
    }


}
