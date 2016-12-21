package net.blay09.mods.waystones.network.message;

import io.netty.buffer.ByteBuf;
import net.blay09.mods.waystones.WarpMode;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageWarpStone implements IMessage {

	private WaystoneEntry waystone;
	private WarpMode warpMode;
	private EnumHand hand;

	public MessageWarpStone() {
	}

	public MessageWarpStone(WaystoneEntry waystone, WarpMode warpMode, EnumHand hand) {
		this.waystone = waystone;
		this.warpMode = warpMode;
		this.hand = hand;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		waystone = WaystoneEntry.read(buf);
		warpMode = WarpMode.values()[buf.readByte()];
		hand = EnumHand.values()[buf.readByte()];
	}

	@Override
	public void toBytes(ByteBuf buf) {
		waystone.write(buf);
		buf.writeByte(warpMode.ordinal());
		buf.writeByte(hand.ordinal());
	}

	public WaystoneEntry getWaystone() {
		return waystone;
	}

	public WarpMode getWarpMode() {
		return warpMode;
	}

	public EnumHand getHand() {
		return hand;
	}

}
