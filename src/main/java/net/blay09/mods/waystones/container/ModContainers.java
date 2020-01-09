package net.blay09.mods.waystones.container;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.core.WarpMode;
import net.blay09.mods.waystones.tileentity.WaystoneTileEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.IForgeRegistry;

public class ModContainers {
    public static ContainerType<WaystoneSelectionContainer> waystoneSelection;

    public static void register(IForgeRegistry<ContainerType<?>> registry) {
        waystoneSelection = new ContainerType<>((IContainerFactory<WaystoneSelectionContainer>) (windowId, inv, data) -> {
            WarpMode warpMode = WarpMode.values[data.readByte()];
            IWaystone fromWaystone = null;
            if (warpMode == WarpMode.WAYSTONE_TO_WAYSTONE) {
                BlockPos pos = data.readBlockPos();
                TileEntity tileEntity = inv.player.world.getTileEntity(pos);
                if (tileEntity instanceof WaystoneTileEntity) {
                    fromWaystone = ((WaystoneTileEntity) tileEntity).getWaystone();
                }
            }

            return new WaystoneSelectionContainer(windowId, warpMode, fromWaystone);
        });
        registry.registerAll(
                waystoneSelection.setRegistryName("waystone_selection")
        );
    }

}
