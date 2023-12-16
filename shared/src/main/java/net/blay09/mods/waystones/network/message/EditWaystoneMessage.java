package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.core.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class EditWaystoneMessage {

    private final UUID waystoneUid;
    private final String name;
    private final WaystoneVisibility visibility;

    public EditWaystoneMessage(UUID waystoneUid, String name, WaystoneVisibility visibility) {
        this.waystoneUid = waystoneUid;
        this.name = name;
        this.visibility = visibility;
    }

    public static void encode(EditWaystoneMessage message, FriendlyByteBuf buf) {
        buf.writeUUID(message.waystoneUid);
        buf.writeUtf(message.name);
        buf.writeEnum(message.visibility);
    }

    public static EditWaystoneMessage decode(FriendlyByteBuf buf) {
        UUID waystoneUid = buf.readUUID();
        String name = buf.readUtf(255);
        final var visibility = buf.readEnum(WaystoneVisibility.class);
        return new EditWaystoneMessage(waystoneUid, name, visibility);
    }

    public static void handle(ServerPlayer player, EditWaystoneMessage message) {
        WaystoneProxy waystone = new WaystoneProxy(player.server, message.waystoneUid);
        WaystoneEditPermissions permissions = PlayerWaystoneManager.mayEditWaystone(player, player.level(), waystone);
        if (permissions != WaystoneEditPermissions.ALLOW) {
            return;
        }

        BlockPos pos = waystone.getPos();
        if (player.distanceToSqr(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f) > 64) {
            return;
        }

        Waystone backingWaystone = (Waystone) waystone.getBackingWaystone();
        String legalName = makeNameLegal(player.server, message.name);
        backingWaystone.setName(legalName);

        if (PlayerWaystoneManager.mayEditGlobalWaystones(player)) {
            if (!backingWaystone.isGlobal() && message.visibility == WaystoneVisibility.GLOBAL) {
                PlayerWaystoneManager.activeWaystoneForEveryone(player.server, backingWaystone);
            }
            backingWaystone.setGlobal(message.visibility == WaystoneVisibility.GLOBAL);
        }

        WaystoneManager.get(player.server).setDirty();
        WaystoneSyncManager.sendWaystoneUpdateToAll(player.server, backingWaystone);

        player.closeContainer();
    }

    private static String makeNameLegal(MinecraftServer server, String name) {
        if (name.trim().isEmpty()) {
            name = Component.translatable("waystones.untitled_waystone").getString();
        }
        String inventoryButtonMode = WaystonesConfig.getActive().inventoryButton.inventoryButton;
        if (inventoryButtonMode.equals(name) && WaystoneManager.get(server).findWaystoneByName(name).isPresent()) {
            return name + "*";
        }

        return name;
    }
}
