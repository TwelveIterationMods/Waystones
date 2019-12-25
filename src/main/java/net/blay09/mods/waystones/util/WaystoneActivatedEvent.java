package net.blay09.mods.waystones.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.eventbus.api.Event;

public class WaystoneActivatedEvent extends Event {
    private String waystoneName;
    private BlockPos pos;
    private DimensionType dimension;

    public WaystoneActivatedEvent(String waystoneName, BlockPos pos, DimensionType dimension) {
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

    public DimensionType getDimension() {
        return dimension;
    }
}
