package net.blay09.mods.waystones.compat;

import mcp.mobius.waila.api.*;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.WaystoneBlockBase;
import net.blay09.mods.waystones.tileentity.WaystoneTileEntityBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

@WailaPlugin(Waystones.MOD_ID)
public class HwylaProvider implements IWailaPlugin {

    @Override
    public void register(IRegistrar registrar) {
        registrar.registerComponentProvider(new WaystoneDataProvider(), TooltipPosition.BODY, WaystoneBlockBase.class);
    }

    private static class WaystoneDataProvider implements IComponentProvider {

        @Override
        public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
            TileEntity tileEntity = accessor.getTileEntity();
            if (tileEntity instanceof WaystoneTileEntityBase) {
                String name = ((WaystoneTileEntityBase) tileEntity).getWaystone().getName();
                tooltip.add( new StringTextComponent(name));
            }
        }

    }
}
