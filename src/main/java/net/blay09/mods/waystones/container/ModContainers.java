package net.blay09.mods.waystones.container;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.client.gui.screen.WarpPlateScreen;
import net.blay09.mods.waystones.core.WarpMode;
import net.blay09.mods.waystones.core.Waystone;
import net.blay09.mods.waystones.tileentity.SharestoneTileEntity;
import net.blay09.mods.waystones.tileentity.WarpPlateTileEntity;
import net.blay09.mods.waystones.tileentity.WaystoneTileEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;

public class ModContainers {
    public static ContainerType<WaystoneSelectionContainer> waystoneSelection;
    public static ContainerType<WaystoneSelectionContainer> sharestoneSelection;
    public static ContainerType<WaystoneSelectionContainer> warpPlateSelection;
    public static ContainerType<WaystoneSettingsContainer> waystoneSettings;

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

            return WaystoneSelectionContainer.createWaystoneSelection(windowId, inv.player, warpMode, fromWaystone);
        });

        sharestoneSelection = new ContainerType<>((IContainerFactory<WaystoneSelectionContainer>) (windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            int count = data.readShort();
            List<IWaystone> waystones = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                waystones.add(Waystone.read(data));
            }

            TileEntity tileEntity = inv.player.world.getTileEntity(pos);
            if (tileEntity instanceof SharestoneTileEntity) {
                IWaystone fromWaystone = ((SharestoneTileEntity) tileEntity).getWaystone();
                return new WaystoneSelectionContainer(ModContainers.sharestoneSelection, WarpMode.SHARESTONE_TO_SHARESTONE, fromWaystone, windowId, waystones);
            }

            return null;
        });

        warpPlateSelection = new ContainerType<>((IContainerFactory<WaystoneSelectionContainer>) (windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            int count = data.readShort();
            List<IWaystone> waystones = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                waystones.add(Waystone.read(data));
            }

            TileEntity tileEntity = inv.player.world.getTileEntity(pos);
            if (tileEntity instanceof WarpPlateTileEntity) {
                IWaystone fromWaystone = ((WarpPlateTileEntity) tileEntity).getWaystone();
                return new WaystoneSelectionContainer(ModContainers.warpPlateSelection, WarpMode.WARP_PLATE, fromWaystone, windowId, waystones);
            }

            return null;
        });

        waystoneSettings = new ContainerType<>((IContainerFactory<WaystoneSettingsContainer>) (windowId, inv, data) -> {
            IWaystone waystone = Waystone.read(data);
            return new WaystoneSettingsContainer(waystoneSettings, waystone, windowId);
        });

        registry.registerAll(
                waystoneSelection.setRegistryName("waystone_selection"),
                sharestoneSelection.setRegistryName("sharestone_selection"),
                warpPlateSelection.setRegistryName("warp_plate_selection"),
                waystoneSettings.setRegistryName("waystone_settings")
        );
    }

}
