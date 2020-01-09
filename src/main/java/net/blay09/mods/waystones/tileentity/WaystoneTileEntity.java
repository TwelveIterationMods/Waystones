package net.blay09.mods.waystones.tileentity;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.container.WaystoneSelectionContainer;
import net.blay09.mods.waystones.core.*;
import net.blay09.mods.waystones.worldgen.namegen.NameGenerator;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorld;

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

    public void initializeWaystone(IWorld world, @Nullable LivingEntity player, boolean wasGenerated) {
        Waystone waystone = new Waystone(UUID.randomUUID(), world.getDimension().getType(), pos, wasGenerated, player != null ? player.getUniqueID() : null);
        String name = NameGenerator.get().getName(waystone, world.getRandom());
        waystone.setName(name);
        WaystoneManager.get().addWaystone(waystone);
        this.waystone = waystone;
    }

    public void initializeFromBase(WaystoneTileEntity tileEntity) {
        waystone = tileEntity.getWaystone();
    }

    public INamedContainerProvider getWaystoneSelectionContainerProvider() {
        return new INamedContainerProvider() {
            @Override
            public ITextComponent getDisplayName() {
                return new TranslationTextComponent("container.waystones.waystone_selection");
            }

            @Override
            public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                return new WaystoneSelectionContainer(i, WarpMode.WAYSTONE_TO_WAYSTONE, getWaystone());
            }
        };
    }

    public INamedContainerProvider getWaystoneSettingsContainerProvider() {
        return new INamedContainerProvider() {
            @Override
            public ITextComponent getDisplayName() {
                return new TranslationTextComponent("container.waystones.waystone_settings");
            }

            @Override
            public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                return new WaystoneSelectionContainer(i, WarpMode.WAYSTONE_TO_WAYSTONE, getWaystone());
            }
        };
    }
}
