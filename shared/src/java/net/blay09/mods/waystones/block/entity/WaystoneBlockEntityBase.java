package net.blay09.mods.waystones.block.entity;

import net.blay09.mods.forbic.block.entity.ForbicBlockEntity;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.block.WaystoneBlock;
import net.blay09.mods.waystones.core.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public abstract class WaystoneBlockEntityBase extends ForbicBlockEntity {

    private IWaystone waystone = InvalidWaystone.INSTANCE;
    private UUID waystoneUid;
    private boolean shouldNotInitialize;
    private boolean silkTouched;

    public WaystoneBlockEntityBase(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        super.save(compound);

        IWaystone waystone = getWaystone();
        if (waystone.isValid()) {
            compound.put("UUID", NbtUtils.createUUID(waystone.getWaystoneUid()));
        }

        return compound;
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        if (compound.contains("UUID", Tag.TAG_INT_ARRAY)) {
            waystoneUid = NbtUtils.loadUUID(Objects.requireNonNull(compound.get("UUID")));
        }
    }

    @Override
    public void forbicOnLoad() {
        IWaystone backingWaystone = waystone;
        if (waystone instanceof WaystoneProxy) {
            backingWaystone = ((WaystoneProxy) waystone).getBackingWaystone();
        }
        if (backingWaystone instanceof Waystone && level != null) {
            ((Waystone) backingWaystone).setDimension(level.dimension());
            ((Waystone) backingWaystone).setPos(worldPosition);
        }
        forbicSync();
    }

    @Override
    public void forbicFromClientTag(CompoundTag tag) {
        IWaystone syncedWaystone = Waystone.read(tag);
        WaystoneManager.get(level.getServer()).updateWaystone(syncedWaystone);
        waystone = new WaystoneProxy(level.getServer(), syncedWaystone.getWaystoneUid());
    }

    @Override
    public CompoundTag forbicToClientTag(CompoundTag tag) {
        Waystone.write(getWaystone(), tag);
        return tag;
    }

    @Override
    public AABB forbicGetRenderBoundingBox() {
        return new AABB(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), worldPosition.getX() + 1, worldPosition.getY() + 2, worldPosition.getZ() + 1);
    }

    public IWaystone getWaystone() {
        if (!waystone.isValid() && level != null && !level.isClientSide && !shouldNotInitialize) {
            if (waystoneUid != null) {
                waystone = new WaystoneProxy(level.getServer(), waystoneUid);
            }

            if (!waystone.isValid()) {
                BlockState state = getBlockState();
                if (state.getBlock() instanceof WaystoneBlock) {
                    DoubleBlockHalf half = state.getValue(WaystoneBlock.HALF);
                    if (half == DoubleBlockHalf.LOWER) {
                        initializeWaystone((ServerLevelAccessor) Objects.requireNonNull(level), null, true);
                    } else if (half == DoubleBlockHalf.UPPER) {
                        BlockEntity blockEntity = level.getBlockEntity(worldPosition.below());
                        if (blockEntity instanceof WaystoneBlockEntityBase) {
                            initializeFromBase(((WaystoneBlockEntityBase) blockEntity));
                        }
                    }
                }
            }

            if (waystone.isValid()) {
                waystoneUid = waystone.getWaystoneUid();
                forbicSync();
            }
        }

        return waystone;
    }

    protected abstract ResourceLocation getWaystoneType();

    public void initializeWaystone(ServerLevelAccessor world, @Nullable LivingEntity player, boolean wasGenerated) {
        Waystone waystone = new Waystone(getWaystoneType(), UUID.randomUUID(), world.getLevel().dimension(), worldPosition, wasGenerated, player != null ? player.getUUID() : null);
        WaystoneManager.get(world.getServer()).addWaystone(waystone);
        this.waystone = waystone;
        setChanged();
        forbicSync();
    }

    public void initializeFromExisting(ServerLevelAccessor world, Waystone existingWaystone, ItemStack itemStack) {
        waystone = existingWaystone;
        existingWaystone.setDimension(world.getLevel().dimension());
        existingWaystone.setPos(worldPosition);
        setChanged();
        forbicSync();
    }

    public void initializeFromBase(WaystoneBlockEntityBase tileEntity) {
        waystone = tileEntity.getWaystone();
        setChanged();
        forbicSync();
    }

    public void uninitializeWaystone() {
        if (waystone.isValid()) {
            WaystoneManager.get(level.getServer()).removeWaystone(waystone);
            PlayerWaystoneManager.removeKnownWaystone(level.getServer(), waystone);
        }

        waystone = InvalidWaystone.INSTANCE;
        shouldNotInitialize = true;

        DoubleBlockHalf half = getBlockState().getValue(WaystoneBlock.HALF);
        BlockPos otherPos = half == DoubleBlockHalf.UPPER ? worldPosition.below() : worldPosition.above();
        BlockEntity blockEntity = Objects.requireNonNull(level).getBlockEntity(otherPos);
        if (blockEntity instanceof WaystoneBlockEntityBase) {
            WaystoneBlockEntityBase waystoneTile = (WaystoneBlockEntityBase) blockEntity;
            waystoneTile.waystone = InvalidWaystone.INSTANCE;
            waystoneTile.shouldNotInitialize = true;
        }

        setChanged();
        forbicSync();
    }

    public void setSilkTouched(boolean silkTouched) {
        this.silkTouched = silkTouched;
    }

    public boolean isSilkTouched() {
        return silkTouched;
    }

    public abstract MenuProvider getMenuProvider();

    @Nullable
    public abstract MenuProvider getSettingsMenuProvider();
}
