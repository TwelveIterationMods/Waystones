package net.blay09.mods.waystones.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;

public class PortstoneTileEntity extends TileEntity {

    public PortstoneTileEntity() {
        super(ModTileEntities.portstone);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1);
    }

}
