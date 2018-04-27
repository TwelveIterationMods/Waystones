package net.blay09.mods.waystones.network.handler;

import net.blay09.mods.waystones.PlayerWaystoneHelper;
import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.MessageFreeWarpReturn;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class HandlerFreeWarpReturn implements IMessageHandler<MessageFreeWarpReturn, IMessage> {
    @Override
    @Nullable
    public IMessage onMessage(MessageFreeWarpReturn message, final MessageContext ctx) {
        NetworkHandler.getThreadListener(ctx).addScheduledTask(() -> {
            if (!WaystoneConfig.general.teleportButton) {
                return;
            }

            EntityPlayer entityPlayer = ctx.getServerHandler().player;
            if (!PlayerWaystoneHelper.canFreeWarp(entityPlayer)) {
                return;
            }

            WaystoneEntry lastWaystone = PlayerWaystoneHelper.getLastWaystone(entityPlayer);
            if (lastWaystone != null && WaystoneManager.teleportToWaystone(entityPlayer, lastWaystone)) {
                if (!lastWaystone.isGlobal() || !WaystoneConfig.general.globalNoCooldown) {
                    PlayerWaystoneHelper.setLastFreeWarp(entityPlayer, System.currentTimeMillis());
                }
            }

            WaystoneManager.sendPlayerWaystones(entityPlayer);

        });
        return null;
    }
}
