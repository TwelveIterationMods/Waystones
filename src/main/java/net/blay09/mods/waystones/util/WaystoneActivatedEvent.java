package net.blay09.mods.waystones.util;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Event;

public class WaystoneActivatedEvent extends Event {
    private String waystoneName;
    private BlockPos pos;
    private int dimension;

    public WaystoneActivatedEvent(String waystoneName, BlockPos pos, int dimension) {
        this.waystoneName = waystoneName;
        this.pos = pos;
        this.dimension = dimension;
    }

    public String getWaystoneName() {
        return waystoneName;
    }

    public BlockPos getPos() {
        return pos;
    }

    public int getDimension() {
        return dimension;
    }
}
