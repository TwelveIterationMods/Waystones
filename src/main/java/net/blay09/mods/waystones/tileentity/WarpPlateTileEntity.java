package net.blay09.mods.waystones.tileentity;

import net.blay09.mods.waystones.block.WarpPlateBlock;
import net.blay09.mods.waystones.container.ModContainers;
import net.blay09.mods.waystones.container.WaystoneSelectionContainer;
import net.blay09.mods.waystones.container.WaystoneSettingsContainer;
import net.blay09.mods.waystones.core.WaystoneTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;


public class WarpPlateTileEntity extends WaystoneTileEntityBase implements ITickableTileEntity {

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

    @Override
    public void tick() {
        if (getBlockState().get(WarpPlateBlock.ACTIVE)) {
            BlockPos pos = getPos();
            AxisAlignedBB boundsAbove = new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
            List<Entity> entities = world.getEntitiesInAABBexcluding(null, boundsAbove, EntityPredicates.IS_ALIVE);
            if (entities.isEmpty()) {
                world.setBlockState(pos, getBlockState().with(WarpPlateBlock.ACTIVE, false), 3);
            }
        }
    }
}
