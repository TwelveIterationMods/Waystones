package net.blay09.mods.waystones.network.handler;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.blay09.mods.waystones.PlayerWaystoneData;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.network.message.MessageWarpReturn;
import net.minecraft.entity.player.EntityPlayer;

public class HandlerFreeWarpReturn implements IMessageHandler<MessageWarpReturn, IMessage> {
	@Override
	public IMessage onMessage(MessageWarpReturn message, final MessageContext ctx) {
		Waystones.proxy.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				if(!Waystones.getConfig().teleportButton) {
					return;
				}
				EntityPlayer entityPlayer = ctx.getServerHandler().playerEntity;
				if(!PlayerWaystoneData.canFreeWarp(entityPlayer)) {
					return;
				}
				if(WaystoneManager.teleportToWaystone(entityPlayer, PlayerWaystoneData.getLastWaystone(entityPlayer))) {
					PlayerWaystoneData.setLastFreeWarp(entityPlayer, System.currentTimeMillis());
				}
				WaystoneManager.sendPlayerWaystones(entityPlayer);

			}
		});
		return null;
	}
}
