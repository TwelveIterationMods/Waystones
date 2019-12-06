package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.GlobalWaystones;
import net.blay09.mods.waystones.PlayerWaystoneHelper;
import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageTeleportToGlobal {

    private final String waystoneName;

    public MessageTeleportToGlobal(String waystoneName) {
        this.waystoneName = waystoneName;
    }

    public static void encode(MessageTeleportToGlobal message, PacketBuffer buf) {
        buf.writeString(message.waystoneName);
    }

    public static MessageTeleportToGlobal decode(PacketBuffer buf) {
        String waystoneName = buf.readString();
        return new MessageTeleportToGlobal(waystoneName);
    }

    public static void handle(MessageTeleportToGlobal message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (WaystoneConfig.SERVER.teleportButtonReturnOnly.get()) {
                return;
            }

            ServerPlayerEntity player = context.getSender();
            if (player == null) {
                return;
            }

            WaystoneEntry waystone = GlobalWaystones.get(player.world).getGlobalWaystone(message.waystoneName);
            if (waystone == null) {
                player.sendStatusMessage(new TranslationTextComponent("waystones:waystoneBroken"), true);
                return;
            }

            int dist = (int) Math.sqrt(player.getDistanceSq(waystone.getPos().getX(), waystone.getPos().getY(), waystone.getPos().getZ()));
            int xpLevelCost = WaystoneConfig.SERVER.blocksPerXPLevel.get() > 0 ? MathHelper.clamp(dist / WaystoneConfig.SERVER.blocksPerXPLevel.get(), 0, WaystoneConfig.SERVER.maximumXpCost.get()) : 0;
            if (!WaystoneConfig.SERVER.teleportButton.get() || WaystoneConfig.SERVER.teleportButtonReturnOnly.get()) {
                return;
            }

            boolean enableXPCost = WaystoneConfig.SERVER.globalWaystonesCostXp.get() && WaystoneConfig.COMMON.inventoryButtonXpCost.get() && !player.abilities.isCreativeMode;
            if (enableXPCost && player.experienceLevel < xpLevelCost) {
                return;
            }

            if (!PlayerWaystoneHelper.canFreeWarp(player)) {
                return;
            }

            if (WaystoneManager.teleportToWaystone(player, waystone)) {
                if (!WaystoneConfig.COMMON.globalNoCooldown.get()) {
                    PlayerWaystoneHelper.setLastFreeWarp(player, System.currentTimeMillis());
                }

                if (enableXPCost) {
                    player.addExperienceLevel(-xpLevelCost);
                }
            }

            WaystoneManager.sendPlayerWaystones(player);
        });
        context.setPacketHandled(true);
    }
}
