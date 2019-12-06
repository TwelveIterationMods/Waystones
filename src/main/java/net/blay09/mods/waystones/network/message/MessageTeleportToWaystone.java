package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.PlayerWaystoneHelper;
import net.blay09.mods.waystones.WarpMode;
import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.item.ModItems;
import net.blay09.mods.waystones.tileentity.WaystoneTileEntity;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class MessageTeleportToWaystone {

    private final WaystoneEntry waystone;
    private final WarpMode warpMode;
    private final Hand hand;
    private final WaystoneEntry fromWaystone;

    public MessageTeleportToWaystone(WaystoneEntry waystone, WarpMode warpMode, Hand hand, @Nullable WaystoneEntry fromWaystone) {
        this.waystone = waystone;
        this.warpMode = warpMode;
        this.hand = hand;
        this.fromWaystone = fromWaystone;
    }

    public static void encode(MessageTeleportToWaystone message, PacketBuffer buf) {
        message.waystone.write(buf);
        buf.writeByte(message.warpMode.ordinal());
        if (message.warpMode == WarpMode.WARP_SCROLL || message.warpMode == WarpMode.WARP_STONE) {
            buf.writeByte(message.hand.ordinal());
        } else if (message.warpMode == WarpMode.WAYSTONE) {
            message.fromWaystone.write(buf);
        }
    }

    public static MessageTeleportToWaystone decode(PacketBuffer buf) {
        WaystoneEntry waystone = WaystoneEntry.read(buf);
        WarpMode warpMode = WarpMode.values()[buf.readByte()];
        Hand hand = (warpMode == WarpMode.WARP_SCROLL || warpMode == WarpMode.WARP_STONE) ? Hand.values()[buf.readByte()] : Hand.MAIN_HAND;
        WaystoneEntry fromWaystone = warpMode == WarpMode.WAYSTONE ? WaystoneEntry.read(buf) : null;
        return new MessageTeleportToWaystone(waystone, warpMode, hand, fromWaystone);
    }

    public static void handle(MessageTeleportToWaystone message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayerEntity player = context.getSender();
            if (player == null) {
                return;
            }

            int dist = (int) Math.sqrt(player.getDistanceSq(message.waystone.getPos().getX(), message.waystone.getPos().getY(), message.waystone.getPos().getZ()));
            WaystoneTileEntity tileWaystone = WaystoneManager.getWaystoneInWorld(message.waystone);
            boolean enableXPCost = WaystoneConfig.SERVER.globalWaystonesCostXp.get() || (tileWaystone != null && !tileWaystone.isGlobal());
            int xpLevelCost = WaystoneConfig.SERVER.blocksPerXPLevel.get() > 0 ? MathHelper.clamp(dist / WaystoneConfig.SERVER.blocksPerXPLevel.get(), 0, WaystoneConfig.SERVER.maximumXpCost.get()) : 0;
            ItemStack heldItem = player.getHeldItem(message.hand);
            switch (message.warpMode) {
                case INVENTORY_BUTTON:
                    if (!WaystoneConfig.SERVER.teleportButton.get() || WaystoneConfig.SERVER.teleportButtonReturnOnly.get() || !WaystoneConfig.COMMON.teleportButtonTarget.get().isEmpty()) {
                        return;
                    }

                    enableXPCost = enableXPCost && WaystoneConfig.COMMON.inventoryButtonXpCost.get() && !player.abilities.isCreativeMode;
                    if (enableXPCost && player.experienceLevel < xpLevelCost) {
                        return;
                    }

                    if (!PlayerWaystoneHelper.canFreeWarp(player)) {
                        return;
                    }

                    break;
                case WARP_SCROLL:
                    if (heldItem.isEmpty() || heldItem.getItem() != ModItems.warpScroll) {
                        return;
                    }

                    break;
                case WARP_STONE:
                    enableXPCost = enableXPCost && WaystoneConfig.COMMON.warpStoneXpCost.get() && !player.abilities.isCreativeMode;
                    if (enableXPCost && player.experienceLevel < xpLevelCost) {
                        return;
                    }

                    if (heldItem.isEmpty() || heldItem.getItem() != ModItems.warpstone) {
                        return;
                    }

                    if (!PlayerWaystoneHelper.canUseWarpStone(player)) {
                        return;
                    }

                    break;
                case WAYSTONE:
                    enableXPCost = enableXPCost && WaystoneConfig.COMMON.waystoneXpCost.get() && !player.abilities.isCreativeMode;
                    if (enableXPCost && player.experienceLevel < xpLevelCost) {
                        return;
                    }

                    WaystoneEntry fromWaystone = message.fromWaystone;
                    if (fromWaystone == null || WaystoneManager.getWaystoneInWorld(fromWaystone) == null) {
                        return;
                    }

                    break;
            }

            if (WaystoneManager.teleportToWaystone(player, message.waystone)) {
                if (enableXPCost) {
                    player.addExperienceLevel(-xpLevelCost);
                }

                // TODO Client can determine if waystone is global or not ... bad
                boolean shouldCooldown = !(message.waystone.isGlobal() && WaystoneConfig.COMMON.globalNoCooldown.get());
                switch (message.warpMode) {
                    case INVENTORY_BUTTON:
                        if (shouldCooldown) {
                            PlayerWaystoneHelper.setLastFreeWarp(player, System.currentTimeMillis());
                        }
                        break;
                    case WARP_SCROLL:
                        heldItem.shrink(1);
                        break;
                    case WARP_STONE:
                        if (shouldCooldown) {
                            PlayerWaystoneHelper.setLastWarpStoneUse(player, System.currentTimeMillis());
                        }
                        break;
                    case WAYSTONE:
                        break;
                }
            }

            WaystoneManager.sendPlayerWaystones(player);
        });
        context.setPacketHandled(true);
    }

}
