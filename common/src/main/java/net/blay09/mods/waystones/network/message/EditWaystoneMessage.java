package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.core.*;
import net.blay09.mods.waystones.menu.WaystoneMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class EditWaystoneMessage implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<EditWaystoneMessage> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(Waystones.MOD_ID,
            "edit_waystone"));

    private final UUID waystoneUid;
    private final String name;
    private final WaystoneVisibility visibility;

    public EditWaystoneMessage(UUID waystoneUid, String name, WaystoneVisibility visibility) {
        this.waystoneUid = waystoneUid;
        this.name = name;
        this.visibility = visibility;
    }

    public static void encode(FriendlyByteBuf buf, EditWaystoneMessage message) {
        buf.writeUUID(message.waystoneUid);
        buf.writeUtf(message.name);
        buf.writeEnum(message.visibility);
    }

    public static EditWaystoneMessage decode(FriendlyByteBuf buf) {
        final var waystoneUid = buf.readUUID();
        final var name = buf.readUtf(255);
        final var visibility = buf.readEnum(WaystoneVisibility.class);
        return new EditWaystoneMessage(waystoneUid, name, visibility);
    }

    public static void handle(ServerPlayer player, EditWaystoneMessage message) {
        final var waystone = new WaystoneProxy(player.server, message.waystoneUid);
        final var error = WaystonePermissionManager.mayEditWaystone(player, player.level(), waystone);
        if (error.isPresent()) {
            return;
        }

        if (!(player.containerMenu instanceof WaystoneMenu settingsMenu)) {
            return;
        }

        var visibility = message.visibility;
        if (!settingsMenu.getVisibilityOptions().contains(message.visibility)) {
            Waystones.logger.warn("{} tried to edit a waystone with an invalid visibility {}", player.getName().getString(), message.visibility);
            visibility = settingsMenu.getVisibilityOptions().get(0);
        }

        if (!WaystonePermissionManager.isAllowedVisibility(visibility) && !WaystonePermissionManager.skipsPermissions(player)) {
            Waystones.logger.warn("{} tried to edit a restricted waystone without permission", player.getName().getString());
            return;
        }

        final var pos = waystone.getPos();
        if (player.distanceToSqr(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f) > 64) {
            return;
        }

        final var backingWaystone = (WaystoneImpl) waystone.getBackingWaystone();
        final var legalName = makeNameLegal(player.server, message.name);
        backingWaystone.setName(legalName);

        if (visibility == WaystoneVisibility.GLOBAL && (WaystonePermissionManager.isAllowedVisibility(visibility) || WaystonePermissionManager.skipsPermissions(
                player))) {
            if (backingWaystone.getVisibility() != WaystoneVisibility.GLOBAL) {
                PlayerWaystoneManager.activeWaystoneForEveryone(player.server, backingWaystone);
            }
        }
        backingWaystone.setVisibility(visibility);

        WaystoneManagerImpl.get(player.server).setDirty();
        WaystoneSyncManager.sendWaystoneUpdateToAll(player.server, backingWaystone);

        player.closeContainer();
    }

    private static Component makeNameLegal(MinecraftServer server, String input) {
        if (input.trim().isEmpty()) {
            return Component.translatable("waystones.untitled_waystone");
        }
        final var inventoryButtonMode = WaystonesConfig.getActive().inventoryButton.inventoryButton;
        if (inventoryButtonMode.equals(input) && WaystoneManagerImpl.get(server).findWaystoneByName(input).isPresent()) {
            return Component.literal(input + "*");
        }

        return Component.literal(input);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
