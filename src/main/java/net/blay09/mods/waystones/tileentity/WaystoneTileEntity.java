package net.blay09.mods.waystones.tileentity;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.block.WaystoneBlock;
import net.blay09.mods.waystones.container.WaystoneSelectionContainer;
import net.blay09.mods.waystones.container.WaystoneSettingsContainer;
import net.blay09.mods.waystones.core.*;
import net.blay09.mods.waystones.worldgen.namegen.NameGenerator;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

public class WaystoneTileEntity extends TileEntity {

    private IWaystone waystone = InvalidWaystone.INSTANCE;
    private boolean shouldNotInitialize;

    public WaystoneTileEntity() {
        super(ModTileEntities.waystone);
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        super.write(tagCompound);

        IWaystone waystone = getWaystone();
        if (waystone.isValid()) {
            tagCompound.put("UUID", NBTUtil.func_240626_a_(waystone.getWaystoneUid()));
        }

        return tagCompound;
    }

    @Override
    public void read(BlockState state, CompoundNBT tagCompound) {
        super.read(state, tagCompound);
        if (tagCompound.contains("UUID", Constants.NBT.TAG_INT_ARRAY)) {
            waystone = new WaystoneProxy(NBTUtil.readUniqueId(Objects.requireNonNull(tagCompound.get("UUID"))));
        }
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        super.onDataPacket(net, pkt);
        read(getBlockState(), pkt.getNbtCompound());
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
        if (!waystone.isValid() && world != null && !world.isRemote && !shouldNotInitialize) {
            BlockState state = getBlockState();
            if (state.getBlock() instanceof WaystoneBlock) {
                DoubleBlockHalf half = state.get(WaystoneBlock.HALF);
                if (half == DoubleBlockHalf.LOWER) {
                    initializeWaystone(Objects.requireNonNull(world), null, true);
                } else if (half == DoubleBlockHalf.UPPER) {
                    TileEntity tileEntity = world.getTileEntity(pos.down());
                    if (tileEntity instanceof WaystoneTileEntity) {
                        initializeFromBase(((WaystoneTileEntity) tileEntity));
                    }
                }
            }
        }

        return waystone;
    }

    public void initializeWaystone(IWorld world, @Nullable LivingEntity player, boolean wasGenerated) {
        Waystone waystone = new Waystone(UUID.randomUUID(), ((World) world).func_234923_W_(), pos, wasGenerated, player != null ? player.getUniqueID() : null);
        String name = NameGenerator.get().getName(waystone, world.getRandom());
        waystone.setName(name);
        WaystoneManager.get().addWaystone(waystone);
        this.waystone = waystone;
    }

    public void initializeFromBase(WaystoneTileEntity tileEntity) {
        waystone = tileEntity.getWaystone();
    }

    public void uninitializeWaystone() {
        if (waystone.isValid()) {
            WaystoneManager.get().removeWaystone(waystone);
            PlayerWaystoneManager.removeKnownWaystone(waystone);
        }

        waystone = InvalidWaystone.INSTANCE;
        shouldNotInitialize = true;

        DoubleBlockHalf half = getBlockState().get(WaystoneBlock.HALF);
        BlockPos otherPos = half == DoubleBlockHalf.UPPER ? pos.down() : pos.up();
        TileEntity tileEntity = Objects.requireNonNull(world).getTileEntity(otherPos);
        if (tileEntity instanceof WaystoneTileEntity) {
            WaystoneTileEntity waystoneTile = (WaystoneTileEntity) tileEntity;
            waystoneTile.waystone = InvalidWaystone.INSTANCE;
            waystoneTile.shouldNotInitialize = true;
        }
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
                return new WaystoneSettingsContainer(i, getWaystone());
            }
        };
    }
}
