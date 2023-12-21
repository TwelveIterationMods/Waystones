package net.blay09.mods.waystones.menu;

import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.menu.BalmMenus;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.entity.SharestoneBlockEntity;
import net.blay09.mods.waystones.block.entity.WarpPlateBlockEntity;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntity;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.blay09.mods.waystones.core.WarpMode;
import net.blay09.mods.waystones.core.Waystone;
import net.blay09.mods.waystones.core.WaystonePermissionManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class ModMenus {
    public static DeferredObject<MenuType<WaystoneSelectionMenu>> waystoneSelection;
    public static DeferredObject<MenuType<WaystoneSelectionMenu>> warpScrollSelection;
    public static DeferredObject<MenuType<WaystoneSelectionMenu>> warpStoneSelection;
    public static DeferredObject<MenuType<WaystoneSelectionMenu>> portstoneSelection;
    public static DeferredObject<MenuType<WaystoneSelectionMenu>> inventorySelection;
    public static DeferredObject<MenuType<WaystoneSelectionMenu>> adminSelection;
    public static DeferredObject<MenuType<WaystoneSelectionMenu>> sharestoneSelection;
    public static DeferredObject<MenuType<WarpPlateMenu>> warpPlate;
    public static DeferredObject<MenuType<WaystoneSettingsMenu>> waystoneSettings;

    public static void initialize(BalmMenus menus) {
        waystoneSelection = menus.registerMenu(id("waystone_selection"), (windowId, inventory, buf) -> {
            final var pos = buf.readBlockPos();
            final var waystones = Waystone.readList(buf);
            final var blockEntity = inventory.player.level().getBlockEntity(pos);
            if (blockEntity instanceof WaystoneBlockEntity waystone) {
                return new WaystoneSelectionMenu(ModMenus.waystoneSelection.get(), WarpMode.WAYSTONE_TO_WAYSTONE, waystone.getWaystone(), windowId, waystones);
            }

            return null;
        });

        warpScrollSelection = menus.registerMenu(id("warp_scroll_selection"), (windowId, inventory, buf) -> {
            final var waystones = Waystone.readList(buf);
            return new WaystoneSelectionMenu(ModMenus.warpScrollSelection.get(), WarpMode.WARP_SCROLL, null, windowId, waystones);
        });

        warpStoneSelection = menus.registerMenu(id("warp_stone_selection"), (windowId, inventory, buf) -> {
            final var waystones = Waystone.readList(buf);
            return new WaystoneSelectionMenu(ModMenus.warpStoneSelection.get(), WarpMode.WARP_STONE, null, windowId, waystones);
        });

        portstoneSelection = menus.registerMenu(id("portstone_selection"), (windowId, inventory, buf) -> {
            final var waystones = Waystone.readList(buf);
            return new WaystoneSelectionMenu(ModMenus.waystoneSelection.get(), WarpMode.PORTSTONE_TO_WAYSTONE, null, windowId, waystones);
        });

        sharestoneSelection = menus.registerMenu(id("sharestone_selection"), (syncId, inventory, buf) -> {
            final var pos = buf.readBlockPos();
            final var waystones = Waystone.readList(buf);

            final var blockEntity = inventory.player.level().getBlockEntity(pos);
            if (blockEntity instanceof SharestoneBlockEntity sharestone) {
                return new WaystoneSelectionMenu(ModMenus.sharestoneSelection.get(),
                        WarpMode.SHARESTONE_TO_SHARESTONE,
                        sharestone.getWaystone(),
                        syncId,
                        waystones);
            }

            return null;
        });

        inventorySelection = menus.registerMenu(id("inventory_selection"), (syncId, inventory, buf) -> {
            final var waystones = Waystone.readList(buf);
            return new WaystoneSelectionMenu(ModMenus.inventorySelection.get(), WarpMode.INVENTORY_BUTTON, null, syncId, waystones);
        });

        adminSelection = menus.registerMenu(id("admin_selection"), (syncId, inventory, buf) -> {
            final var waystones = Waystone.readList(buf);
            return new WaystoneSelectionMenu(ModMenus.adminSelection.get(), WarpMode.CUSTOM, null, syncId, waystones);
        });

        warpPlate = menus.registerMenu(id("warp_plate"), (windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();

            BlockEntity blockEntity = inv.player.level().getBlockEntity(pos);
            if (blockEntity instanceof WarpPlateBlockEntity warpPlate) {
                return new WarpPlateMenu(windowId, warpPlate, warpPlate.getContainerData(), inv);
            }

            return null;
        });

        waystoneSettings = menus.registerMenu(id("waystone_settings"), (windowId, inv, data) -> {
            final var pos = data.readBlockPos();
            final var waystone = Waystone.read(data);
            final var canEdit = data.readBoolean();

            BlockEntity blockEntity = inv.player.level().getBlockEntity(pos);
            if (blockEntity instanceof WaystoneBlockEntityBase waystoneBlockEntity) {
                return new WaystoneSettingsMenu(windowId, waystone, waystoneBlockEntity, waystoneBlockEntity.getContainerData(), inv, canEdit);
            }

            return null;

        });
    }

    @NotNull
    private static ResourceLocation id(String name) {
        return new ResourceLocation(Waystones.MOD_ID, name);
    }

}
