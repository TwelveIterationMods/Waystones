package net.blay09.mods.waystones.tileentity;

import net.blay09.mods.waystones.api.IAttunementItem;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.block.WarpPlateBlock;
import net.blay09.mods.waystones.container.WarpPlateContainer;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WarpMode;
import net.blay09.mods.waystones.core.WaystoneTypes;
import net.blay09.mods.waystones.item.AttunedShardItem;
import net.blay09.mods.waystones.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;


public class WarpPlateTileEntity extends WaystoneTileEntityBase implements ITickableTileEntity {

    private final WeakHashMap<Entity, Integer> ticksPassedPerEntity = new WeakHashMap<>();
    private final ItemStackHandler itemStackHandler = new ItemStackHandler(5) {
        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (!completedFirstAttunement) {
                return ItemStack.EMPTY;
            }

            return super.extractItem(slot, amount, simulate);
        }
    };

    private final IntReferenceHolder attunementTicks = IntReferenceHolder.single();

    private boolean readyForAttunement;
    private boolean completedFirstAttunement;

    public WarpPlateTileEntity() {
        super(ModTileEntities.warpPlate);

        itemStackHandler.setStackInSlot(0, new ItemStack(Items.FLINT));
        itemStackHandler.setStackInSlot(1, new ItemStack(ModItems.warpDust));
        itemStackHandler.setStackInSlot(2, new ItemStack(ModItems.warpDust));
        itemStackHandler.setStackInSlot(3, new ItemStack(ModItems.warpDust));
        itemStackHandler.setStackInSlot(4, new ItemStack(ModItems.warpDust));
    }

    @Override
    protected ResourceLocation getWaystoneType() {
        return WaystoneTypes.WARP_PLATE;
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        super.write(tagCompound);

        tagCompound.put("Items", itemStackHandler.serializeNBT());
        tagCompound.putBoolean("ReadyForAttunement", readyForAttunement);
        tagCompound.putBoolean("CompletedFirstAttunement", completedFirstAttunement);

        return tagCompound;
    }

    @Override
    public void read(BlockState state, CompoundNBT tagCompound) {
        super.read(state, tagCompound);

        itemStackHandler.deserializeNBT(tagCompound.getCompound("Items"));
        readyForAttunement = tagCompound.getBoolean("ReadyForAttunement");
        completedFirstAttunement = tagCompound.getBoolean("CompletedFirstAttunement");
    }

    @Override
    public INamedContainerProvider getWaystoneSelectionContainerProvider() {
        return new INamedContainerProvider() {
            @Override
            public ITextComponent getDisplayName() {
                return new TranslationTextComponent("container.waystones.warp_plate");
            }

            @Override
            public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                return new WarpPlateContainer(i, WarpPlateTileEntity.this, playerInventory);
            }
        };
    }

    @Override
    public INamedContainerProvider getWaystoneSettingsContainerProvider() {
        return null;
    }

    public void onEntityCollision(Entity entity) {
        ticksPassedPerEntity.putIfAbsent(entity, 0);
    }

    private boolean isEntityOnWarpPlate(Entity entity) {
        return entity.getPosX() >= pos.getX() && entity.getPosX() < pos.getX() + 1
                && entity.getPosY() >= pos.getY() && entity.getPosY() < pos.getY() + 1
                && entity.getPosZ() >= pos.getZ() && entity.getPosZ() < pos.getZ() + 1;
    }

    @Override
    public void tick() {
        if (!world.isRemote) {
            if (isReadyForAttunement()) {
                attunementTicks.set(attunementTicks.get() + 1);

                if (attunementTicks.get() >= getMaxAttunementTicks()) {
                    attunementTicks.set(0);
                    ItemStack attunedShard = new ItemStack(ModItems.attunedShard);
                    AttunedShardItem.setWaystoneAttunedTo(attunedShard, getWaystone());
                    itemStackHandler.setStackInSlot(0, attunedShard);
                    for (int i = 1; i <= 4; i++) {
                        itemStackHandler.setStackInSlot(i, ItemStack.EMPTY);
                    }
                    completedFirstAttunement = true;
                }
            } else {
                attunementTicks.set(0);
            }

            if (getBlockState().get(WarpPlateBlock.ACTIVE)) {
                BlockPos pos = getPos();
                AxisAlignedBB boundsAbove = new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
                List<Entity> entities = world.getEntitiesInAABBexcluding(null, boundsAbove, EntityPredicates.IS_ALIVE);
                if (entities.isEmpty()) {
                    world.setBlockState(pos, getBlockState().with(WarpPlateBlock.ACTIVE, false), 3);
                    ticksPassedPerEntity.clear();
                }
            }

            Iterator<Map.Entry<Entity, Integer>> iterator = ticksPassedPerEntity.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Entity, Integer> entry = iterator.next();
                Entity entity = entry.getKey();
                Integer ticksPassed = entry.getValue();
                if (!entity.isAlive()) {
                    iterator.remove();
                } else if (!isEntityOnWarpPlate(entity)) {
                    iterator.remove();
                } else if (ticksPassed > 20) {
                    IWaystone targetWaystone = getTargetWaystone();
                    if (targetWaystone != null) {
                        PlayerWaystoneManager.tryTeleportToWaystone(entity, targetWaystone, WarpMode.WARP_PLATE, getWaystone());
                    } else if (entity instanceof PlayerEntity) {
                        TranslationTextComponent chatComponent = new TranslationTextComponent("chat.waystones.warp_plate_has_no_target");
                        chatComponent.mergeStyle(TextFormatting.DARK_RED);
                        ((PlayerEntity) entity).sendStatusMessage(chatComponent, true);
                    }
                    iterator.remove();
                } else if (ticksPassed != -1) {
                    entry.setValue(ticksPassed + 1);
                }
            }
        }

        if (itemStackHandler.getStackInSlot(0).getItem() != Items.FLINT) {
            completedFirstAttunement = true;
        }
    }

    private boolean isReadyForAttunement() {
        return readyForAttunement
                && itemStackHandler.getStackInSlot(0).getItem() == Items.FLINT
                && itemStackHandler.getStackInSlot(1).getItem() == ModItems.warpDust
                && itemStackHandler.getStackInSlot(2).getItem() == ModItems.warpDust
                && itemStackHandler.getStackInSlot(3).getItem() == ModItems.warpDust
                && itemStackHandler.getStackInSlot(4).getItem() == ModItems.warpDust;
    }

    @Nullable
    public IWaystone getTargetWaystone() {
        ItemStack attunedItem = itemStackHandler.getStackInSlot(0);
        if (attunedItem.getItem() instanceof IAttunementItem) {
            return ((IAttunementItem) attunedItem.getItem()).getWaystoneAttunedTo(attunedItem);
        }

        return null;
    }

    public ItemStackHandler getItemStackHandler() {
        return itemStackHandler;
    }

    public int getMaxAttunementTicks() {
        return 30;
    }

    public IntReferenceHolder getAttunementTicks() {
        return attunementTicks;
    }

    public void markReadyForAttunement() {
        readyForAttunement = true;
    }

    public void markEntityForCooldown(Entity entity) {
        ticksPassedPerEntity.put(entity, -1);
    }

}
