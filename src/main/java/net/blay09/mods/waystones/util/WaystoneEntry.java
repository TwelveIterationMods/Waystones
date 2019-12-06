package net.blay09.mods.waystones.util;

import net.blay09.mods.waystones.tileentity.WaystoneTileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.registries.ForgeRegistries;

public class WaystoneEntry {

    private final String name;
    private final DimensionType dimension;
    private final BlockPos pos;
    private boolean isGlobal;

    public WaystoneEntry(String name, DimensionType dimension, BlockPos pos, boolean isGlobal) {
        this.name = name;
        this.dimension = dimension;
        this.pos = pos;
        this.isGlobal = isGlobal;
    }

    public WaystoneEntry(WaystoneTileEntity tileWaystone) {
        this.name = tileWaystone.getWaystoneName();
        this.dimension = tileWaystone.getWorld().getDimension().getType();
        this.pos = tileWaystone.getPos();
        this.isGlobal = tileWaystone.isGlobal();
    }

    public String getName() {
        return name;
    }

    public DimensionType getDimension() {
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
        String name = buf.readString();
        DimensionType dimension = DimensionType.getById(buf.readInt());
        BlockPos pos = BlockPos.fromLong(buf.readLong());
        boolean isGlobal = buf.readBoolean();
        return new WaystoneEntry(name, dimension, pos, isGlobal);
    }

    public static WaystoneEntry read(CompoundNBT tagCompound) {
        DimensionType dimension = DimensionType.getById(tagCompound.getInt("Dimension"));
        String name = tagCompound.getString("Name");
        BlockPos pos = BlockPos.fromLong(tagCompound.getLong("Position"));
        boolean isGlobal = tagCompound.getBoolean("IsGlobal");
        return new WaystoneEntry(name, dimension, pos, isGlobal);
    }

    public void write(PacketBuffer buf) {
        buf.writeString(name);
        buf.writeInt(dimension.getId());
        buf.writeLong(pos.toLong());
        buf.writeBoolean(isGlobal);
    }

    public CompoundNBT writeToNBT() {
        CompoundNBT tagCompound = new CompoundNBT();
        tagCompound.putString("Name", name);
        tagCompound.putInt("Dimension", dimension.getId());
        tagCompound.putLong("Position", pos.toLong());
        tagCompound.putBoolean("IsGlobal", isGlobal);
        return tagCompound;
    }


}
