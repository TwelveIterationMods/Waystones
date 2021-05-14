package net.blay09.mods.waystones.tileentity;

import net.blay09.mods.waystones.block.SharestoneBlock;
import net.blay09.mods.waystones.container.ModContainers;
import net.blay09.mods.waystones.container.WaystoneSelectionContainer;
import net.blay09.mods.waystones.container.WaystoneSettingsContainer;
import net.blay09.mods.waystones.core.WaystoneTypes;
import net.blay09.mods.waystones.worldgen.namegen.NameGenerationMode;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;


public class SharestoneTileEntity extends WaystoneTileEntityBase {

    public SharestoneTileEntity() {
        super(ModTileEntities.sharestone);
    }

    @Override
    protected ResourceLocation getWaystoneType() {
        return WaystoneTypes.getSharestone(((SharestoneBlock) getBlockState().getBlock()).getColor());
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
                return WaystoneSelectionContainer.createSharestoneSelection(i, getWaystone(), getBlockState());
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

}
