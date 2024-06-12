package net.blay09.mods.waystones.block.entity;

import net.blay09.mods.balm.api.block.entity.CustomRenderBoundingBox;
import net.blay09.mods.balm.api.block.entity.OnLoadHandler;
import net.blay09.mods.balm.api.container.BalmContainerProvider;
import net.blay09.mods.balm.api.container.DefaultContainer;
import net.blay09.mods.balm.api.container.ExtractionAwareContainer;
import net.blay09.mods.balm.common.BalmBlockEntity;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneOrigin;
import net.blay09.mods.waystones.api.WaystonesAPI;
import net.blay09.mods.waystones.block.WaystoneBlock;
import net.blay09.mods.waystones.block.WaystoneBlockBase;
import net.blay09.mods.waystones.component.ModComponents;
import net.blay09.mods.waystones.core.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
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

import java.util.*;

public abstract class WaystoneBlockEntityBase extends BalmBlockEntity implements OnLoadHandler, CustomRenderBoundingBox, BalmContainerProvider {

    protected final DefaultContainer container = new DefaultContainer(5);

    private Waystone waystone = InvalidWaystone.INSTANCE;
    private UUID waystoneUid;
    private boolean shouldNotInitialize;
    private boolean silkTouched;

    public WaystoneBlockEntityBase(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        tag.put("Items", container.serialize(provider));

        if (waystone.isValid()) {
            tag.put("UUID", NbtUtils.createUUID(waystone.getWaystoneUid()));
        } else if (waystoneUid != null) {
            tag.put("UUID", NbtUtils.createUUID(waystoneUid));
        }
    }

    @Override
    public void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        container.deserialize(compound.getCompound("Items"), provider);

        if (compound.contains("UUID", Tag.TAG_INT_ARRAY)) {
            waystoneUid = NbtUtils.loadUUID(Objects.requireNonNull(compound.get("UUID")));
        }

        if (compound.contains("Waystone", Tag.TAG_COMPOUND)) {
            var syncedWaystone = WaystoneImpl.read(compound.getCompound("Waystone"), provider);
            WaystoneManagerImpl.get(null).updateWaystone(syncedWaystone);
            waystone = new WaystoneProxy(null, syncedWaystone.getWaystoneUid());
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput input) {
        final var waystoneUidComponent = input.get(ModComponents.waystone.get());
        if (waystoneUidComponent != null) {
            waystoneUid = waystoneUidComponent;
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        builder.set(ModComponents.waystone.get(), waystone.isValid() ? waystone.getWaystoneUid() : waystoneUid);
    }

    @Override
    public void writeUpdateTag(CompoundTag tag) {
        tag.put("Waystone", WaystoneImpl.write(getWaystone(), new CompoundTag(), level.registryAccess()));
    }

    @Override
    public void onLoad() {
        Waystone backingWaystone = waystone;
        if (waystone instanceof WaystoneProxy) {
            backingWaystone = ((WaystoneProxy) waystone).getBackingWaystone();
        }
        if (backingWaystone instanceof WaystoneImpl && level != null) {
            ((WaystoneImpl) backingWaystone).setDimension(level.dimension());
            ((WaystoneImpl) backingWaystone).setPos(worldPosition);
        }
        sync();
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition.getX(),
                worldPosition.getY(),
                worldPosition.getZ(),
                worldPosition.getX() + 1,
                worldPosition.getY() + 2,
                worldPosition.getZ() + 1);
    }

    public Waystone getWaystone() {
        if (!waystone.isValid() && level != null && !level.isClientSide && !shouldNotInitialize) {
            if (waystoneUid != null) {
                waystone = new WaystoneProxy(level.getServer(), waystoneUid);
            }

            if (!waystone.isValid()) {
                BlockState state = getBlockState();
                if (state.getBlock() instanceof WaystoneBlockBase) {
                    DoubleBlockHalf half = state.hasProperty(WaystoneBlockBase.HALF) ? state.getValue(WaystoneBlockBase.HALF) : DoubleBlockHalf.LOWER;
                    WaystoneOrigin origin = state.hasProperty(WaystoneBlockBase.ORIGIN) ? state.getValue(WaystoneBlockBase.ORIGIN) : WaystoneOrigin.UNKNOWN;
                    if (half == DoubleBlockHalf.LOWER) {
                        initializeWaystone((ServerLevelAccessor) Objects.requireNonNull(level), null, origin);
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
                sync();
            }
        }

        return waystone;
    }

    protected abstract ResourceLocation getWaystoneType();

    public void initializeWaystone(ServerLevelAccessor world, @Nullable LivingEntity player, WaystoneOrigin origin) {
        WaystoneImpl waystone = new WaystoneImpl(getWaystoneType(),
                UUID.randomUUID(),
                world.getLevel().dimension(),
                worldPosition,
                origin,
                player != null ? player.getUUID() : null);
        WaystoneManagerImpl.get(world.getServer()).addWaystone(waystone);
        this.waystone = waystone;
        setChanged();
        sync();
    }

    public void initializeFromExisting(ServerLevelAccessor world, WaystoneImpl existingWaystone, ItemStack itemStack) {
        waystone = existingWaystone;
        existingWaystone.setDimension(world.getLevel().dimension());
        existingWaystone.setPos(worldPosition);
        setChanged();
        sync();
    }

    public void initializeFromBase(WaystoneBlockEntityBase tileEntity) {
        waystone = tileEntity.getWaystone();
        setChanged();
        sync();
    }

    public void uninitializeWaystone() {
        if (waystone.isValid()) {
            WaystoneManagerImpl.get(level.getServer()).removeWaystone(waystone);
            PlayerWaystoneManager.removeKnownWaystone(level.getServer(), waystone);
            WaystoneSyncManager.sendWaystoneRemovalToAll(level.getServer(), waystone, true);
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
        sync();
    }

    public void setSilkTouched(boolean silkTouched) {
        this.silkTouched = silkTouched;
    }

    public boolean isSilkTouched() {
        return silkTouched;
    }

    public Optional<MenuProvider> getSelectionMenuProvider() {
        return Optional.empty();
    }

    public Optional<MenuProvider> getSettingsMenuProvider() {
        return Optional.empty();
    }

    public Optional<MenuProvider> getModifierMenuProvider() {
        return Optional.empty();
    }

    public Collection<? extends Waystone> getAuxiliaryTargets() {
        final var result = new ArrayList<Waystone>();
        for (int i = 0; i < container.getContainerSize(); i++) {
            final var item = container.getItem(i);
            WaystonesAPI.getBoundWaystone(null, item).ifPresent(result::add);
        }
        return result;
    }

    @Override
    public Container getContainer() {
        return container;
    }
}
