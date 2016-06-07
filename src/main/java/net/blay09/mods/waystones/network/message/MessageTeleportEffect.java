package net.blay09.mods.waystones.network.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import net.blay09.mods.waystones.util.BlockPos;

public class MessageTeleportEffect implements IMessage {

	private BlockPos pos;

	public MessageTeleportEffect() {
	}

	public MessageTeleportEffect(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = BlockPos.fromLong(buf.readLong());
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(pos.toLong());
	}

	public BlockPos getPos() {
		return pos;
	}

}
