package net.blay09.mods.waystones.network.handler;

import net.blay09.mods.waystones.PlayerWaystoneData;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.MessageWaystones;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerWaystones implements IMessageHandler<MessageWaystones, IMessage> {
	@Override
	public IMessage onMessage(final MessageWaystones message, final MessageContext ctx) {
		NetworkHandler.getThreadListener(ctx).addScheduledTask(new Runnable() {
			@Override
			public void run() {
				WaystoneManager.setKnownWaystones(message.getEntries());
				WaystoneManager.setServerWaystones(message.getServerEntries());
				PlayerWaystoneData.store(FMLClientHandler.instance().getClientPlayerEntity(), message.getEntries(), message.getLastServerWaystoneName(), message.getLastFreeWarp(), message.getLastWarpStoneUse());
			}
		});
		return null;
	}
}
