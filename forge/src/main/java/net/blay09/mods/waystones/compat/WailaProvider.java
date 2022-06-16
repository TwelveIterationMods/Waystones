package net.blay09.mods.waystones.compat;

import mcp.mobius.waila.api.*;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.block.WaystoneBlockBase;
import net.blay09.mods.waystones.block.entity.WarpPlateBlockEntity;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WaystoneTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

@WailaPlugin(id = Waystones.MOD_ID)
public class WailaProvider implements IWailaPlugin {

    @Override
    public void register(IRegistrar registrar) {
        registrar.registerComponentProvider(new WaystoneDataProvider(), TooltipPosition.BODY, WaystoneBlockBase.class);
    }

    private static class WaystoneDataProvider implements IComponentProvider {

        @Override
        public void appendBody(List<Component> tooltip, IDataAccessor accessor, IPluginConfig config) {
            BlockEntity tileEntity = accessor.getBlockEntity();
            if (tileEntity instanceof WarpPlateBlockEntity) {
                /* Hwyla does not use the correct galactic font, so don't display for warp plates.
                IWaystone waystone = ((WarpPlateTileEntity) tileEntity).getWaystone();
                tooltip.add(WarpPlateBlock.getGalacticName(waystone)); */
            } else if (tileEntity instanceof WaystoneBlockEntityBase) {
                IWaystone waystone = ((WaystoneBlockEntityBase) tileEntity).getWaystone();
                boolean isActivated = !waystone.getWaystoneType().equals(WaystoneTypes.WAYSTONE) || PlayerWaystoneManager.isWaystoneActivated(accessor.getPlayer(), waystone);
                if (isActivated && waystone.hasName() && waystone.isValid()) {
                    tooltip.add(Component.literal(waystone.getName()));
                } else {
                    tooltip.add(Component.translatable("tooltip.waystones.undiscovered"));
                }
            }
        }

    }
}
