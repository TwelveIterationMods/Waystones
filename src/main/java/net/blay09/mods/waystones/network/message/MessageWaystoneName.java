package net.blay09.mods.waystones.network.message;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import net.blay09.mods.waystones.util.BlockPos;

public class MessageWaystoneName implements IMessage {

	private BlockPos pos;
	private String name;
	private boolean isGlobal;

	public MessageWaystoneName() {
	}

	public MessageWaystoneName(BlockPos pos, String name, boolean isGlobal) {
		this.pos = pos;
		this.name = name;
		this.isGlobal = isGlobal;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = BlockPos.fromLong(buf.readLong());
		name = ByteBufUtils.readUTF8String(buf);
		isGlobal = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(pos.toLong());
		ByteBufUtils.writeUTF8String(buf, name);
		buf.writeBoolean(isGlobal);
	}

	public BlockPos getPos() {
		return pos;
	}

	public String getName() {
		return name;
	}

	public boolean isGlobal() {
		return isGlobal;
	}
}
