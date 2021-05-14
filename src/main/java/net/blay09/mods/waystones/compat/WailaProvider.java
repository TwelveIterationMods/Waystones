package net.blay09.mods.waystones.compat;

import mcp.mobius.waila.api.*;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.block.WarpPlateBlock;
import net.blay09.mods.waystones.block.WaystoneBlockBase;
import net.blay09.mods.waystones.tileentity.WarpPlateTileEntity;
import net.blay09.mods.waystones.tileentity.WaystoneTileEntityBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

@WailaPlugin(Waystones.MOD_ID)
public class WailaProvider implements IWailaPlugin {

    @Override
    public void register(IRegistrar registrar) {
        registrar.registerComponentProvider(new WaystoneDataProvider(), TooltipPosition.BODY, WaystoneBlockBase.class);
    }

    private static class WaystoneDataProvider implements IComponentProvider {

        @Override
        public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
            TileEntity tileEntity = accessor.getTileEntity();
            if (tileEntity instanceof WarpPlateTileEntity) {
                /* Hwyla does not use the correct galactic font, so don't display for warp plates.
                IWaystone waystone = ((WarpPlateTileEntity) tileEntity).getWaystone();
                tooltip.add(WarpPlateBlock.getGalacticName(waystone)); */
            } else if (tileEntity instanceof WaystoneTileEntityBase) {
                IWaystone waystone = ((WaystoneTileEntityBase) tileEntity).getWaystone();
                if (waystone.hasName()) {
                    tooltip.add(new StringTextComponent(waystone.getName()));
                } else {
                    tooltip.add(new TranslationTextComponent("tooltip.waystones.undiscovered"));
                }
            }
        }

    }
}
