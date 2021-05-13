package net.blay09.mods.waystones.tileentity;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.block.WarpPlateBlock;
import net.blay09.mods.waystones.container.ModContainers;
import net.blay09.mods.waystones.container.WaystoneSelectionContainer;
import net.blay09.mods.waystones.container.WaystoneSettingsContainer;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WarpMode;
import net.blay09.mods.waystones.core.WaystoneTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;


public class WarpPlateTileEntity extends WaystoneTileEntityBase implements ITickableTileEntity {

    private final WeakHashMap<Entity, Integer> ticksPassedPerEntity = new WeakHashMap<>();

    public WarpPlateTileEntity() {
        super(ModTileEntities.warpPlate);
    }

    @Override
    protected ResourceLocation getWaystoneType() {
        return WaystoneTypes.WARP_PLATE;
    }

    @Override
    public INamedContainerProvider getWaystoneSelectionContainerProvider() {
        return new INamedContainerProvider() {
            @Override
            public ITextComponent getDisplayName() {
                return new TranslationTextComponent("container.waystones.waystone_selection");
            }

            @Override
            public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                return WaystoneSelectionContainer.createWarpPlateSelection(i, getWaystone());
            }
        };
    }

    @Override
    public INamedContainerProvider getWaystoneSettingsContainerProvider() {
        return new INamedContainerProvider() {
            @Override
            public ITextComponent getDisplayName() {
                return new TranslationTextComponent("container.waystones.waystone_settings");
            }

            @Override
            public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                return new WaystoneSettingsContainer(ModContainers.waystoneSettings, getWaystone(), i);
            }
        };
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
                IWaystone targetWaystone = getWaystone().getTargetWaystone();
                if (targetWaystone != null) {
                    PlayerWaystoneManager.tryTeleportToWaystone(entity, targetWaystone, WarpMode.WARP_PLATE, getWaystone());
                } else if(entity instanceof PlayerEntity) {
                    TranslationTextComponent chatComponent = new TranslationTextComponent("chat.waystones.warp_plate_has_no_target");
                    chatComponent.mergeStyle(TextFormatting.DARK_RED);
                    ((PlayerEntity) entity).sendStatusMessage(chatComponent, true);
                }
                iterator.remove();
            } else {
                entry.setValue(ticksPassed + 1);
            }
        }
    }
}
