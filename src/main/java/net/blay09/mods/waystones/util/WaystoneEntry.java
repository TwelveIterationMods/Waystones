package net.blay09.mods.waystones.util;

import net.blay09.mods.waystones.tileentity.WaystoneTileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.Dimension;

public class WaystoneEntry {

    private final String name;
    private final Dimension dimension;
    private final BlockPos pos;
    private boolean isGlobal;

    public WaystoneEntry(String name, Dimension dimension, BlockPos pos, boolean isGlobal) {
        this.name = name;
        this.dimension = dimension;
        this.pos = pos;
        this.isGlobal = isGlobal;
    }

    public WaystoneEntry(WaystoneTileEntity tileWaystone) {
        this.name = tileWaystone.getWaystoneName();
        this.dimension = tileWaystone.getWorld().getDimension();
        this.pos = tileWaystone.getPos();
        this.isGlobal = tileWaystone.isGlobal();
    }

    public String getName() {
        return name;
    }

    public Dimension getDimension() {
        return dimension;
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

    public static WaystoneEntry read(PacketBuffer buf) {
        Dimension dimension = null; // TODO
        return new WaystoneEntry(buf.readString(), dimension, BlockPos.fromLong(buf.readLong()), buf.readBoolean());
    }

    public static WaystoneEntry read(CompoundNBT tagCompound) {
        Dimension dimension = null; // TODO
        return new WaystoneEntry(tagCompound.getString("Name"), dimension, BlockPos.fromLong(tagCompound.getLong("Position")), tagCompound.getBoolean("IsGlobal"));
    }

    public void write(PacketBuffer buf) {
        buf.writeString(name);
        // TODO buf.writeInt(dimension);
        buf.writeLong(pos.toLong());
        buf.writeBoolean(isGlobal);
    }

    public CompoundNBT writeToNBT() {
        CompoundNBT tagCompound = new CompoundNBT();
        tagCompound.putString("Name", name);
        // TODO tagCompound.putInt("Dimension", dimension);
        tagCompound.putLong("Position", pos.toLong());
        tagCompound.putBoolean("IsGlobal", isGlobal);
        return tagCompound;
    }


}
