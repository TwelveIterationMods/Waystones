package net.blay09.mods.waystones.network.message;

import net.blay09.mods.forbic.menu.ForbicMenuProvider;
import net.blay09.mods.forbic.network.ForbicNetworking;
import net.blay09.mods.waystones.core.Waystone;
import net.blay09.mods.waystones.menu.ModMenus;
import net.blay09.mods.waystones.menu.WaystoneSettingsMenu;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WaystoneEditPermissions;
import net.blay09.mods.waystones.core.WaystoneProxy;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.UUID;

public class RequestEditWaystoneMessage {

    private final UUID waystoneUid;

    public RequestEditWaystoneMessage(UUID waystoneUid) {
        this.waystoneUid = waystoneUid;
    }

    public static void encode(RequestEditWaystoneMessage message, FriendlyByteBuf buf) {
        buf.writeUUID(message.waystoneUid);
    }

    public static RequestEditWaystoneMessage decode(FriendlyByteBuf buf) {
        UUID waystoneUid = buf.readUUID();
        return new RequestEditWaystoneMessage(waystoneUid);
    }

    public static void handle(ServerPlayer player, RequestEditWaystoneMessage message) {
        WaystoneProxy waystone = new WaystoneProxy(player.server, message.waystoneUid);
        WaystoneEditPermissions permissions = PlayerWaystoneManager.mayEditWaystone(player, player.level, waystone);
        if (permissions != WaystoneEditPermissions.ALLOW) {
            return;
        }

        BlockPos pos = waystone.getPos();
        if (player.distanceToSqr(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f) > 64) {
            return;
        }

        final ForbicMenuProvider containerProvider = new ForbicMenuProvider() {
            @Override
            public Component getDisplayName() {
                return new TranslatableComponent("container.waystones.waystone_settings");
            }

            @Override
            public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
                return new WaystoneSettingsMenu(ModMenus.waystoneSettings.get(), waystone, i);
            }

            @Override
            public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                Waystone.write(buf, waystone);
            }
        };
        ForbicNetworking.openGui(player, containerProvider);
    }
}

