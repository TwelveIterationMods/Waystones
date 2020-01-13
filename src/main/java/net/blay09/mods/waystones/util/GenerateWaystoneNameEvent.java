package net.blay09.mods.waystones.util;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Event;

public class GenerateWaystoneNameEvent extends Event {
    private final BlockPos pos;
    private final int dimension;
    private String waystoneName;

    public GenerateWaystoneNameEvent(BlockPos pos, int dimension, String waystoneName) {
        this.waystoneName = waystoneName;
        this.pos = pos;
        this.dimension = dimension;
    }

    public String getWaystoneName() {
        return waystoneName;
    }

    public void setWaystoneName(String waystoneName) {
        this.waystoneName = waystoneName;
    }

    public BlockPos getPos() {
        return pos;
    }

    public int getDimension() {
        return dimension;
    }
}
