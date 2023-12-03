package net.blay09.mods.waystones.menu;

import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.menu.BalmMenus;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.block.entity.SharestoneBlockEntity;
import net.blay09.mods.waystones.block.entity.WarpPlateBlockEntity;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntity;
import net.blay09.mods.waystones.core.WarpMode;
import net.blay09.mods.waystones.core.Waystone;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ModMenus {
    public static DeferredObject<MenuType<WaystoneSelectionMenu>> waystoneSelection;
    public static DeferredObject<MenuType<WaystoneSelectionMenu>> adminSelection;
    public static DeferredObject<MenuType<WaystoneSelectionMenu>> sharestoneSelection;
    public static DeferredObject<MenuType<WarpPlateContainer>> warpPlate;
    public static DeferredObject<MenuType<WaystoneSettingsMenu>> waystoneSettings;

    public static void initialize(BalmMenus menus) {
        waystoneSelection = menus.registerMenu(id("waystone_selection"), (syncId, inventory, buf) -> {
            WarpMode warpMode = WarpMode.values[buf.readByte()];
            IWaystone fromWaystone = null;
            if (warpMode == WarpMode.WAYSTONE_TO_WAYSTONE) {
                BlockPos pos = buf.readBlockPos();
                BlockEntity blockEntity = inventory.player.level().getBlockEntity(pos);
                if (blockEntity instanceof WaystoneBlockEntity) {
                    fromWaystone = ((WaystoneBlockEntity) blockEntity).getWaystone();
                }
            }

            return WaystoneSelectionMenu.createWaystoneSelection(syncId, inventory.player, warpMode, fromWaystone);
        });

        sharestoneSelection = menus.registerMenu(id("sharestone_selection"), (syncId, inventory, buf) -> {
            BlockPos pos = buf.readBlockPos();
            int count = buf.readShort();
            List<IWaystone> waystones = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                waystones.add(Waystone.read(buf));
            }

            BlockEntity blockEntity = inventory.player.level().getBlockEntity(pos);
            if (blockEntity instanceof SharestoneBlockEntity) {
                IWaystone fromWaystone = ((SharestoneBlockEntity) blockEntity).getWaystone();
                return new WaystoneSelectionMenu(ModMenus.sharestoneSelection.get(), WarpMode.SHARESTONE_TO_SHARESTONE, fromWaystone, syncId, waystones);
            }

            return null;
        });

        adminSelection = menus.registerMenu(id("waystone_op_selection"), (syncId, inventory, buf) -> {
            if (!inventory.player.hasPermissions(2)) {
                return null;
            }
            int count = buf.readInt();
            List<IWaystone> waystones = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                waystones.add(Waystone.read(buf));
            }

            return new WaystoneSelectionMenu(ModMenus.adminSelection.get(), WarpMode.CUSTOM, null, syncId, waystones);
        });

        warpPlate = menus.registerMenu(id("warp_plate"), (windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();

            BlockEntity blockEntity = inv.player.level().getBlockEntity(pos);
            if (blockEntity instanceof WarpPlateBlockEntity warpPlate) {
                return new WarpPlateContainer(windowId, warpPlate, warpPlate.getContainerData(), inv);
            }

            return null;
        });

        waystoneSettings = menus.registerMenu(id("waystone_settings"), (windowId, inv, data) -> {
            IWaystone waystone = Waystone.read(data);
            return new WaystoneSettingsMenu(waystoneSettings.get(), waystone, windowId);
        });
    }

    @NotNull
    private static ResourceLocation id(String name) {
        return new ResourceLocation(Waystones.MOD_ID, name);
    }

}
