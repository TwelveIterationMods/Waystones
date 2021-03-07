package net.blay09.mods.waystones.network.handler;

import net.blay09.mods.waystones.PlayerWaystoneData;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.MessageSortWaystone;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class HandlerSortWaystone implements IMessageHandler<MessageSortWaystone, IMessage> {
	@Override
	@Nullable
	public IMessage onMessage(final MessageSortWaystone message, final MessageContext ctx) {
		NetworkHandler.getThreadListener(ctx).addScheduledTask(() -> {
			PlayerWaystoneData waystoneData = PlayerWaystoneData.fromPlayer(ctx.getServerHandler().player);
			WaystoneEntry[] entries = waystoneData.getWaystones();
			int index = message.getIndex();
			int otherIndex = message.getOtherIndex();
			if(index < 0 || index >= entries.length || otherIndex < 0 || otherIndex >= entries.length) {
				return;
			}
			if (otherIndex == 0 && index != 1) {
				WaystoneEntry[] result = new WaystoneEntry[entries.length];
				System.arraycopy(entries, 0, result, 1, index);
				System.arraycopy(entries, index + 1, result, index + 1, entries.length - index - 1);
				result[0] = entries[index];
				System.arraycopy(result, 0, entries, 0, entries.length);
			} else if (otherIndex == entries.length - 1 && index != entries.length - 2) {
				WaystoneEntry[] result = new WaystoneEntry[entries.length];
				System.arraycopy(entries, 0, result, 0, index);
				System.arraycopy(entries, index+1, result, index, entries.length - index - 1);
				result[entries.length - 1] = entries[index];
				System.arraycopy(result, 0, entries, 0, entries.length);
			} else {
				WaystoneEntry swap = entries[index];
				entries[index] = entries[otherIndex];
				entries[otherIndex] = swap;
			}
			waystoneData.store(ctx.getServerHandler().player);
			WaystoneManager.sendPlayerWaystones(ctx.getServerHandler().player);
		});
		return null;
	}
}
