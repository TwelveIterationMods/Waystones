package net.blay09.mods.waystones.util;

import io.netty.buffer.ByteBuf;
import net.blay09.mods.waystones.block.TileWaystone;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class WaystoneEntry {

	private final String name;
	private final int dimensionId;
	private final BlockPos pos;
	private boolean isGlobal;

	public WaystoneEntry(String name, int dimensionId, BlockPos pos, boolean isGlobal) {
		this.name = name;
		this.dimensionId = dimensionId;
		this.pos = pos;
		this.isGlobal = isGlobal;
	}

	public WaystoneEntry(TileWaystone tileWaystone) {
		this.name = tileWaystone.getWaystoneName();
		this.dimensionId = tileWaystone.getWorld().provider.getDimension();
		this.pos = tileWaystone.getPos();
		this.isGlobal = tileWaystone.isGlobal();
	}

	public String getName() {
		return name;
	}

	public int getDimensionId() {
		return dimensionId;
	}

	public BlockPos getPos() {
		return pos;
	}

	public boolean isGlobal() {
		return isGlobal;
	}

	public void setGlobal(boolean isGlobal) {
		this.isGlobal = isGlobal;
	}

	public static WaystoneEntry read(ByteBuf buf) {
		return new WaystoneEntry(ByteBufUtils.readUTF8String(buf), buf.readInt(), BlockPos.fromLong(buf.readLong()), buf.readBoolean());
	}

	public static WaystoneEntry read(CompoundNBT tagCompound) {
		return new WaystoneEntry(tagCompound.getString("Name"), tagCompound.getInt("Dimension"), BlockPos.fromLong(tagCompound.getLong("Position")), tagCompound.getBoolean("IsGlobal"));
	}

	public void write(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, name);
		buf.writeInt(dimensionId);
		buf.writeLong(pos.toLong());
		buf.writeBoolean(isGlobal);
	}

	public CompoundNBT writeToNBT() {
		CompoundNBT tagCompound = new CompoundNBT();
		tagCompound.putString("Name", name);
		tagCompound.putInt("Dimension", dimensionId);
		tagCompound.putLong("Position", pos.toLong());
		tagCompound.putBoolean("IsGlobal", isGlobal);
		return tagCompound;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		WaystoneEntry that = (WaystoneEntry) o;
		return dimensionId == that.dimensionId && pos.equals(that.pos) && isGlobal == that.isGlobal;
	}

	@Override
	public int hashCode() {
		int result = dimensionId;
		result = 31 * result + pos.hashCode();
		return result;
	}

}
