package net.blay09.mods.waystones.network.message;

import io.netty.buffer.ByteBuf;
import net.blay09.mods.waystones.WarpMode;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import javax.annotation.Nullable;

public class MessageTeleportToWaystone implements IMessage {

	private WaystoneEntry waystone;
	private WarpMode warpMode;
	private EnumHand hand;
	private WaystoneEntry fromWaystone;

	public MessageTeleportToWaystone() {
	}

	public MessageTeleportToWaystone(WaystoneEntry waystone, WarpMode warpMode, EnumHand hand, @Nullable WaystoneEntry fromWaystone) {
		this.waystone = waystone;
		this.warpMode = warpMode;
		this.hand = hand;
		this.fromWaystone = fromWaystone;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		waystone = WaystoneEntry.read(buf);
		warpMode = WarpMode.values()[buf.readByte()];
		hand = (warpMode == WarpMode.WARP_SCROLL || warpMode == WarpMode.WARP_STONE) ? EnumHand.values()[buf.readByte()] : EnumHand.MAIN_HAND;
		if(warpMode == WarpMode.WAYSTONE) {
			fromWaystone = WaystoneEntry.read(buf);
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		waystone.write(buf);
		buf.writeByte(warpMode.ordinal());
		if(warpMode == WarpMode.WARP_SCROLL || warpMode == WarpMode.WARP_STONE) {
			buf.writeByte(hand.ordinal());
		} else if(warpMode == WarpMode.WAYSTONE) {
			fromWaystone.write(buf);
		}
	}

	public WaystoneEntry getWaystone() {
		return waystone;
	}

	@Nullable
	public WaystoneEntry getFromWaystone() {
		return fromWaystone;
	}

	public WarpMode getWarpMode() {
		return warpMode;
	}

	public EnumHand getHand() {
		return hand;
	}

}
