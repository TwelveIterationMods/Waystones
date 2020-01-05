package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WarpMode;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageInventoryButton {

    public static void encode(MessageInventoryButton message, PacketBuffer buf) {
    }

    public static MessageInventoryButton decode(PacketBuffer buf) {
        return new MessageInventoryButton();
    }

    public static void handle(MessageInventoryButton message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (!WaystoneConfig.SERVER.teleportButton.get()) {
                return;
            }

            PlayerEntity player = context.getSender();
            if (player == null) {
                return;
            }

            if (!PlayerWaystoneManager.canUseInventoryButton(player)) {
                return;
            }

            IWaystone nearestWaystone = PlayerWaystoneManager.getNearestWaystone(player);
            if (nearestWaystone != null && PlayerWaystoneManager.tryTeleportToWaystone(player, nearestWaystone, WarpMode.INVENTORY_BUTTON, ItemStack.EMPTY, null)) {
                if (!nearestWaystone.isGlobal() || !WaystoneConfig.COMMON.globalNoCooldown.get()) {
                    PlayerWaystoneManager.setLastInventoryWarp(player, System.currentTimeMillis());
                }
            }
        });
        context.setPacketHandled(true);
    }

}
