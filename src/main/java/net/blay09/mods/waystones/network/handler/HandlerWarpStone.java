package net.blay09.mods.waystones.network.handler;

import net.blay09.mods.waystones.PlayerWaystoneData;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.MessageWarpStone;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerWarpStone implements IMessageHandler<MessageWarpStone, IMessage> {
	@Override
	public IMessage onMessage(final MessageWarpStone message, final MessageContext ctx) {
		NetworkHandler.getThreadListener(ctx).addScheduledTask(new Runnable() {
			@Override
			public void run() {
				if(message.isFree()) {
					if(!Waystones.getConfig().teleportButton || Waystones.getConfig().teleportButtonReturnOnly || !PlayerWaystoneData.canFreeWarp(ctx.getServerHandler().playerEntity)) {
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
