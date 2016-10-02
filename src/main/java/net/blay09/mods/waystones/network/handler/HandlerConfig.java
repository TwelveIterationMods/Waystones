package net.blay09.mods.waystones.network.handler;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.MessageConfig;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class HandlerConfig implements IMessageHandler<MessageConfig, IMessage> {
	@Override
	public IMessage onMessage(final MessageConfig message, MessageContext ctx) {
		NetworkHandler.getThreadListener(ctx).addScheduledTask(new Runnable() {
			@Override
			public void run() {
				Waystones.instance.setConfig(message.getConfig());
			}
		});
		return null;
	}
}
