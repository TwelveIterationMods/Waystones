package net.blay09.mods.waystones.network.handler;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.network.message.MessageConfig;

public class HandlerConfig implements IMessageHandler<MessageConfig, IMessage> {
	@Override
	public IMessage onMessage(final MessageConfig message, MessageContext ctx) {
		Waystones.proxy.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				Waystones.instance.setConfig(message.getConfig());
			}
		});
		return null;
	}
}
