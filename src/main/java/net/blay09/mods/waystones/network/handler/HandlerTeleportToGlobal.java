package net.blay09.mods.waystones.network.handler;

import net.blay09.mods.waystones.*;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.MessageTeleportToGlobal;
import net.blay09.mods.waystones.network.message.MessageTeleportToWaystone;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class HandlerTeleportToGlobal implements IMessageHandler<MessageTeleportToGlobal, IMessage> {

    @Override
    @Nullable
    public IMessage onMessage(final MessageTeleportToGlobal message, final MessageContext ctx) {
        NetworkHandler.getThreadListener(ctx).addScheduledTask(() -> {
            if (WaystoneConfig.general.teleportButtonReturnOnly) {
                return;
            }

            EntityPlayer player = ctx.getServerHandler().player;
            WaystoneEntry waystone = GlobalWaystones.get(player.world).getGlobalWaystone(message.getWaystoneName());
            if (waystone == null) {
                player.sendStatusMessage(new TextComponentTranslation("waystones:waystoneBroken"), true);
                return;
            }

            int dist = (int) Math.sqrt(player.getDistanceSqToCenter(waystone.getPos()));
            int xpLevelCost = WaystoneConfig.general.blocksPerXPLevel > 0 ? MathHelper.clamp(dist / WaystoneConfig.general.blocksPerXPLevel, 0, WaystoneConfig.general.maximumXpCost) : 0;
            if (!WaystoneConfig.general.teleportButton || WaystoneConfig.general.teleportButtonReturnOnly) {
                return;
            }

            boolean enableXPCost = WaystoneConfig.general.globalWaystonesCostXp && WaystoneConfig.general.inventoryButtonXpCost;
            if (enableXPCost && player.experienceLevel < xpLevelCost) {
                return;
            }

            if (!PlayerWaystoneHelper.canFreeWarp(ctx.getServerHandler().player)) {
                return;
            }

            if (WaystoneManager.teleportToWaystone(ctx.getServerHandler().player, waystone)) {
                if (!WaystoneConfig.general.globalNoCooldown) {
                    PlayerWaystoneHelper.setLastFreeWarp(ctx.getServerHandler().player, System.currentTimeMillis());
                }

                if (enableXPCost) {
                    player.addExperienceLevel(-xpLevelCost);
                }
            }

            WaystoneManager.sendPlayerWaystones(ctx.getServerHandler().player);
        });
        return null;
    }

}
