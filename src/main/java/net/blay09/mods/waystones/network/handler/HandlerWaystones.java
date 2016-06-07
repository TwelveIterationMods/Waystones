package net.blay09.mods.waystones.network.handler;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.blay09.mods.waystones.PlayerWaystoneData;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.network.message.MessageWaystones;

public class HandlerWaystones implements IMessageHandler<MessageWaystones, IMessage> {
	@Override
	public IMessage onMessage(final MessageWaystones message, final MessageContext ctx) {
		Waystones.proxy.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				WaystoneManager.setKnownWaystones(message.getEntries());
				WaystoneManager.setServerWaystones(message.getServerEntries());
				PlayerWaystoneData.store(FMLClientHandler.instance().getClientPlayerEntity(), message.getEntries(), message.getLastFreeWarp(), message.getLastWarpStoneUse());
			}
		});
		return null;
	}
}
