package net.blay09.mods.waystones.network.handler;

import net.blay09.mods.waystones.PlayerWaystoneData;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.MessageSortWaystone;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;

public class HandlerSortWaystone implements IMessageHandler<MessageSortWaystone, IMessage> {
	@Override
	@Nullable
	public IMessage onMessage(final MessageSortWaystone message, final MessageContext ctx) {
		NetworkHandler.getThreadListener(ctx).addScheduledTask(new Runnable() {
			@Override
			public void run() {
				PlayerWaystoneData waystoneData = PlayerWaystoneData.fromPlayer(ctx.getServerHandler().playerEntity);
				WaystoneEntry[] entries = waystoneData.getWaystones();
				int index = ArrayUtils.indexOf(entries, message.getIndex());
				int otherIndex = ArrayUtils.indexOf(entries, message.getIndex());
				if(index == -1 || otherIndex == -1) {
					return;
				}
				WaystoneEntry swap = entries[index];
				entries[index] = entries[otherIndex];
				entries[otherIndex] = swap;
				waystoneData.store(ctx.getServerHandler().playerEntity);
			}
		});
		return null;
	}
}
