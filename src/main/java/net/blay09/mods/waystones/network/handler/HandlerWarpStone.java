package net.blay09.mods.waystones.network.handler;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.blay09.mods.waystones.PlayerWaystoneData;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.network.message.MessageWarpStone;

public class HandlerWarpStone implements IMessageHandler<MessageWarpStone, IMessage> {
	@Override
	public IMessage onMessage(final MessageWarpStone message, final MessageContext ctx) {
		Waystones.proxy.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				if(message.isFree()) {
					if(!Waystones.getConfig().teleportButton || Waystones.getConfig().teleportButtonReturnOnly || PlayerWaystoneData.canFreeWarp(ctx.getServerHandler().playerEntity)) {
						return;
					}
				}
				if(WaystoneManager.teleportToWaystone(ctx.getServerHandler().playerEntity, message.getWaystone())) {
					if(WaystoneManager.getServerWaystone(message.getWaystone().getName()) == null || !Waystones.getConfig().globalNoCooldown) {
						if (message.isFree()) {
							PlayerWaystoneData.setLastFreeWarp(ctx.getServerHandler().playerEntity, System.currentTimeMillis());
						} else {
							PlayerWaystoneData.setLastWarpStoneUse(ctx.getServerHandler().playerEntity, System.currentTimeMillis());
						}
					}
				}
				WaystoneManager.sendPlayerWaystones(ctx.getServerHandler().playerEntity);
			}
		});
		return null;
	}
}
