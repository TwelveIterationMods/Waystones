package net.blay09.mods.waystones.network.message;

import io.netty.buffer.ByteBuf;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageWarpStone implements IMessage {

	private WaystoneEntry waystone;
	private boolean isFree;

	public MessageWarpStone() {
	}

	public MessageWarpStone(WaystoneEntry waystone, boolean isFree) {
		this.waystone = waystone;
		this.isFree = isFree;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		waystone = WaystoneEntry.read(buf);
		isFree = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		waystone.write(buf);
		buf.writeBoolean(isFree);
	}

	public WaystoneEntry getWaystone() {
		return waystone;
	}

	public boolean isFree() {
		return isFree;
	}

}
