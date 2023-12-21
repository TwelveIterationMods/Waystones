package net.blay09.mods.waystones.block.entity;

import net.blay09.mods.balm.api.block.entity.CustomRenderBoundingBox;
import net.blay09.mods.balm.api.block.entity.OnLoadHandler;
import net.blay09.mods.balm.api.container.ImplementedContainer;
import net.blay09.mods.balm.common.BalmBlockEntity;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.api.WaystoneOrigin;
import net.blay09.mods.waystones.api.WaystonesAPI;
import net.blay09.mods.waystones.block.WaystoneBlock;
import net.blay09.mods.waystones.block.WaystoneBlockBase;
import net.blay09.mods.waystones.core.*;
import net.blay09.mods.waystones.recipe.ModRecipes;
import net.blay09.mods.waystones.recipe.WaystoneRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

public abstract class WaystoneBlockEntityBase extends BalmBlockEntity implements OnLoadHandler, CustomRenderBoundingBox, ImplementedContainer {

    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int i) {
            return attunementTicks;
        }

        @Override
        public void set(int i, int j) {
            attunementTicks = j;
        }

        @Override
        public int getCount() {
            return 1;
        }
    };

    private final NonNullList<ItemStack> items = NonNullList.withSize(5, ItemStack.EMPTY);

    private boolean readyForAttunement;
    private boolean completedFirstAttunement;

    protected int attunementTicks;
    private IWaystone waystone = InvalidWaystone.INSTANCE;
    private UUID waystoneUid;
    private boolean shouldNotInitialize;
    private boolean silkTouched;

    public WaystoneBlockEntityBase(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        ContainerHelper.saveAllItems(tag, items);

        if (waystone.isValid()) {
            tag.put("UUID", NbtUtils.createUUID(waystone.getWaystoneUid()));
        } else if (waystoneUid != null) {
            tag.put("UUID", NbtUtils.createUUID(waystoneUid));
        }

        tag.putBoolean("ReadyForAttunement", readyForAttunement);
        tag.putBoolean("CompletedFirstAttunement", completedFirstAttunement);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);

        ContainerHelper.loadAllItems(compound, items);

        if (compound.contains("UUID", Tag.TAG_INT_ARRAY)) {
            waystoneUid = NbtUtils.loadUUID(Objects.requireNonNull(compound.get("UUID")));
        }

        if (compound.contains("Waystone", Tag.TAG_COMPOUND)) {
            var syncedWaystone = Waystone.read(compound.getCompound("Waystone"));
            WaystoneManager.get(null).updateWaystone(syncedWaystone);
            waystone = new WaystoneProxy(null, syncedWaystone.getWaystoneUid());
        }

        readyForAttunement = compound.getBoolean("ReadyForAttunement");
        completedFirstAttunement = compound.getBoolean("CompletedFirstAttunement");
    }

    @Override
    public void writeUpdateTag(CompoundTag tag) {
        tag.put("Waystone", Waystone.write(getWaystone(), new CompoundTag()));
    }

    @Override
    public void onLoad() {
        IWaystone backingWaystone = waystone;
        if (waystone instanceof WaystoneProxy) {
            backingWaystone = ((WaystoneProxy) waystone).getBackingWaystone();
        }
        if (backingWaystone instanceof Waystone && level != null) {
            ((Waystone) backingWaystone).setDimension(level.dimension());
            ((Waystone) backingWaystone).setPos(worldPosition);
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

    public IWaystone getWaystone() {
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
        Waystone waystone = new Waystone(getWaystoneType(),
                UUID.randomUUID(),
                world.getLevel().dimension(),
                worldPosition,
                origin,
                player != null ? player.getUUID() : null);
        WaystoneManager.get(world.getServer()).addWaystone(waystone);
        this.waystone = waystone;
        setChanged();
        sync();

        if (!isCompletedFirstAttunement()) {
            initializeInventory(world);
        }
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        if (!isCompletedFirstAttunement()) {
            return ItemStack.EMPTY;
        }
        return ImplementedContainer.super.removeItem(slot, count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (!isCompletedFirstAttunement()) {
            return ItemStack.EMPTY;
        }

        return ImplementedContainer.super.removeItemNoUpdate(slot);
    }

    public void initializeFromExisting(ServerLevelAccessor world, Waystone existingWaystone, ItemStack itemStack) {
        waystone = existingWaystone;
        existingWaystone.setDimension(world.getLevel().dimension());
        existingWaystone.setPos(worldPosition);
        setChanged();
        sync();

        CompoundTag tag = itemStack.getTag();
        completedFirstAttunement = tag != null && tag.getBoolean("CompletedFirstAttunement");

        if (!isCompletedFirstAttunement()) {
            initializeInventory(world);
        }
    }

    public void initializeFromBase(WaystoneBlockEntityBase tileEntity) {
        waystone = tileEntity.getWaystone();
        setChanged();
        sync();
    }

    public void uninitializeWaystone() {
        if (waystone.isValid()) {
            WaystoneManager.get(level.getServer()).removeWaystone(waystone);
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

    public abstract MenuProvider getMenuProvider();

    @Nullable
    public abstract MenuProvider getSettingsMenuProvider();

    public int getMaxAttunementTicks() {
        return 30;
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return items;
    }

    public ContainerData getContainerData() {
        return dataAccess;
    }

    @Nullable
    protected WaystoneRecipe trySelectRecipe() {
        if (!readyForAttunement) {
            return null;
        }
        if (level == null) {
            return null;
        }

        // Prevent crafting when more than one item is present in center slot
        if (getItem(0).getCount() > 1) {
            return null;
        }

        // Prevent crafting if center slot is already attuned
        if (WaystonesAPI.getBoundWaystone(null, getItem(0)).isPresent()) {
            return null;
        }

        return level.getRecipeManager().getRecipeFor(ModRecipes.waystoneRecipeType, this, level)
                .map(RecipeHolder::value).orElse(null);
    }

    public void serverTick() {
        WaystoneRecipe recipe = trySelectRecipe();
        if (recipe != null) {
            attunementTicks++;

            if (attunementTicks >= getMaxAttunementTicks()) {
                attunementTicks = 0;
                craft(recipe);
            }
        } else {
            attunementTicks = 0;
        }
    }

    protected void craft(WaystoneRecipe recipe) {
        ItemStack attunedShard = recipe.assemble(this, RegistryAccess.EMPTY);
        WaystonesAPI.setBoundWaystone(attunedShard, getWaystone());
        ItemStack centerStack = getItem(0);
        if (centerStack.getCount() > 1) {
            centerStack = centerStack.copyWithCount(centerStack.getCount() - 1);
            if (!Minecraft.getInstance().player.getInventory().add(centerStack)) {
                Minecraft.getInstance().player.drop(centerStack, false);
            }
        }
        setItem(0, attunedShard);
        for (int i = 1; i <= 4; i++) {
            getItem(i).shrink(1);
        }

        this.completedFirstAttunement = true;
    }

    public Collection<? extends IWaystone> getAuxiliaryTargets() {
        final var result = new ArrayList<IWaystone>();
        for (final var item : getItems()) {
            WaystonesAPI.getBoundWaystone(null, item).ifPresent(result::add);
        }
        return result;
    }

    public boolean shouldPerformInitialAttunement() {
        return false;
    }

    public boolean isCompletedFirstAttunement() {
        return !shouldPerformInitialAttunement() || completedFirstAttunement;
    }

    /**
     * We delay attunement until the menu is opened to show the player what's happening inside the slots before converting the items to an attuned shard.
     */
    public void markReadyForAttunement() {
        readyForAttunement = true;
    }

    private void initializeInventory(ServerLevelAccessor levelAccessor) {
        WaystoneRecipe initializingRecipe = levelAccessor.getLevel().getRecipeManager().getAllRecipesFor(ModRecipes.waystoneRecipeType)
                .stream()
                .filter(holder -> holder.id().getNamespace().equals(Waystones.MOD_ID) && holder.id().getPath().equals("attuned_shard"))
                .map(RecipeHolder::value)
                .findFirst()
                .orElse(null);
        if (initializingRecipe == null) {
            Waystones.logger.error("Failed to find Attunement recipe for initial attunement");
            completedFirstAttunement = true;
            return;
        }

        for (int i = 0; i < 5; i++) {
            final var ingredient = initializingRecipe.getIngredients().get(i);
            final var ingredientItems = ingredient.getItems();
            final var ingredientItem = ingredientItems.length > 0 ? ingredientItems[0] : ItemStack.EMPTY;
            setItem(i, ingredientItem.copy());
        }
    }
}
