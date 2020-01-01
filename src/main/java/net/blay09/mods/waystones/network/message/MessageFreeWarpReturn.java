package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.PlayerWaystoneHelper;
import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.WaystoneManagerLegacy;
import net.blay09.mods.waystones.api.IWaystone;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageFreeWarpReturn {

    public static void encode(MessageFreeWarpReturn message, PacketBuffer buf) {
    }

    public static MessageFreeWarpReturn decode(PacketBuffer buf) {
        return new MessageFreeWarpReturn();
    }

    public static void handle(MessageFreeWarpReturn message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (!WaystoneConfig.SERVER.teleportButton.get()) {
                return;
            }

            PlayerEntity player = context.getSender();
            if (player == null) {
                return;
            }

            if (!PlayerWaystoneHelper.canFreeWarp(player)) {
                return;
            }

            IWaystone lastWaystone = PlayerWaystoneHelper.getNearestWaystone(player);
            if (lastWaystone != null && WaystoneManagerLegacy.teleportToWaystone(player, lastWaystone)) {
                if (!lastWaystone.isGlobal() || !WaystoneConfig.COMMON.globalNoCooldown.get()) {
                    PlayerWaystoneHelper.setLastFreeWarp(player, System.currentTimeMillis());
                }
            }
        });
        context.setPacketHandled(true);
    }

}
