package net.blay09.mods.waystones.tileentity;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.core.InvalidWaystone;
import net.blay09.mods.waystones.core.Waystone;
import net.blay09.mods.waystones.core.WaystoneProxy;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

public class WaystoneTileEntity extends TileEntity {

    private IWaystone waystone;

    public WaystoneTileEntity() {
        super(ModTileEntities.waystone);
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        super.write(tagCompound);
        tagCompound.put("UUID", NBTUtil.writeUniqueId(getWaystone().getWaystoneUid()));
        return tagCompound;
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
        waystone = new WaystoneProxy(NBTUtil.readUniqueId(tagCompound.getCompound("UUID")));
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        super.onDataPacket(net, pkt);
        read(pkt.getNbtCompound());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return write(new CompoundNBT());
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(pos, 0, getUpdateTag());
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1);
    }

    public IWaystone getWaystone() {
        return waystone != null ? waystone : InvalidWaystone.INSTANCE;
    }

    public void initializePlacedBy(World world, @Nullable LivingEntity player) {
        waystone = new Waystone(UUID.randomUUID(), "", world.dimension.getType(), pos, false, player != null ? player.getUniqueID() : null);
    }

    public void initializeGenerated(IWorld world, String generatedName) {
        waystone = new Waystone(UUID.randomUUID(), generatedName, world.getDimension().getType(), pos, true, null);
    }

    public void initializeFromBase(WaystoneTileEntity tileEntity) {
        waystone = tileEntity.getWaystone();
    }
}
