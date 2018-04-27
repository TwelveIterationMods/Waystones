package net.blay09.mods.waystones.network.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import net.blay09.mods.waystones.WarpMode;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import javax.annotation.Nullable;

public class MessageTeleportToGlobal implements IMessage {

	private String waystoneName;

	public MessageTeleportToGlobal() {
	}

	public MessageTeleportToGlobal(String waystoneName) {
		this.waystoneName = waystoneName;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		waystoneName = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, waystoneName);
	}

	public String getWaystoneName() {
		return waystoneName;
	}

}
