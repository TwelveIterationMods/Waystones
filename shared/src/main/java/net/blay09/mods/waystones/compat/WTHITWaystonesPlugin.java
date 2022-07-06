package net.blay09.mods.waystones.compat;

import mcp.mobius.waila.api.*;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.block.WarpPlateBlock;
import net.blay09.mods.waystones.block.WaystoneBlockBase;
import net.blay09.mods.waystones.block.entity.WarpPlateBlockEntity;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WaystoneTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;

public class WTHITWaystonesPlugin implements IWailaPlugin {

    @Override
    public void register(IRegistrar registrar) {
        registrar.addComponent(new WaystoneDataProvider(), TooltipPosition.BODY, WaystoneBlockBase.class);
    }

    private static class WaystoneDataProvider implements IBlockComponentProvider {

        @Override
        public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
            BlockEntity tileEntity = accessor.getBlockEntity();
            if (tileEntity instanceof WarpPlateBlockEntity warpPlate) {
                /* Hwyla does not use the correct galactic font, so don't display for warp plates. TODO */
                IWaystone waystone = warpPlate.getWaystone();
                tooltip.addLine(WarpPlateBlock.getGalacticName(waystone));
            } else if (tileEntity instanceof WaystoneBlockEntityBase waystoneBlockEntity) {
                IWaystone waystone = waystoneBlockEntity.getWaystone();
                boolean isActivated = !waystone.getWaystoneType().equals(WaystoneTypes.WAYSTONE) || PlayerWaystoneManager.isWaystoneActivated(accessor.getPlayer(), waystone);
                if (isActivated && waystone.hasName() && waystone.isValid()) {
                    tooltip.addLine(Component.literal(waystone.getName()));
                } else {
                    tooltip.addLine(Component.translatable("tooltip.waystones.undiscovered"));
                }
            }
        }

    }
}
