package net.blay09.mods.waystones.network.message;

import io.netty.buffer.ByteBuf;
import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageWaystones implements IMessage {

	private WaystoneEntry[] entries;
	private long lastFreeWarp;
	private long lastWarpStoneUse;

	public MessageWaystones() {
	}

	public MessageWaystones(WaystoneEntry[] entries, long lastFreeWarp, long lastWarpStoneUse) {
		this.entries = entries;
		this.lastFreeWarp = lastFreeWarp;
		this.lastWarpStoneUse = lastWarpStoneUse;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		entries = new WaystoneEntry[buf.readByte()];
		for(int i = 0; i < entries.length; i++) {
			entries[i] = WaystoneEntry.read(buf);
		}
		lastFreeWarp = buf.readLong();
		lastWarpStoneUse = buf.readLong();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeByte(entries.length);
		for (WaystoneEntry entry : entries) {
			entry.write(buf);
		}
		buf.writeLong(lastFreeWarp);
		buf.writeLong(Math.max(0, WaystoneConfig.general.warpStoneCooldown * 1000 - (System.currentTimeMillis() - lastWarpStoneUse)));
	}

	public WaystoneEntry[] getEntries() {
		return entries;
	}

	public long getLastFreeWarp() {
		return lastFreeWarp;
	}

	public long getLastWarpStoneUse() {
		return lastWarpStoneUse;
	}
}
