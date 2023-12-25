package net.blay09.mods.waystones.menu;

import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.menu.BalmMenus;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.TeleportFlags;
import net.blay09.mods.waystones.block.entity.SharestoneBlockEntity;
import net.blay09.mods.waystones.block.entity.WarpPlateBlockEntity;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntity;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.blay09.mods.waystones.core.WaystoneImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Set;

public class ModMenus {
    public static DeferredObject<MenuType<WaystoneSelectionMenu>> waystoneSelection;
    public static DeferredObject<MenuType<WaystoneSelectionMenu>> warpScrollSelection;
    public static DeferredObject<MenuType<WaystoneSelectionMenu>> warpStoneSelection;
    public static DeferredObject<MenuType<WaystoneSelectionMenu>> portstoneSelection;
    public static DeferredObject<MenuType<WaystoneSelectionMenu>> inventorySelection;
    public static DeferredObject<MenuType<WaystoneSelectionMenu>> adminSelection;
    public static DeferredObject<MenuType<WaystoneSelectionMenu>> sharestoneSelection;
    public static DeferredObject<MenuType<WarpPlateMenu>> warpPlate;
    public static DeferredObject<MenuType<WaystoneMenu>> waystoneSettings;

    public static void initialize(BalmMenus menus) {
        waystoneSelection = menus.registerMenu(id("waystone_selection"), (windowId, inventory, buf) -> {
            final var pos = buf.readBlockPos();
            final var waystones = WaystoneImpl.readList(buf);
            final var blockEntity = inventory.player.level().getBlockEntity(pos);
            if (blockEntity instanceof WaystoneBlockEntity waystone) {
                return new WaystoneSelectionMenu(ModMenus.waystoneSelection.get(), waystone.getWaystone(), windowId, waystones, Collections.emptySet());
            }

            return null;
        });

        warpScrollSelection = menus.registerMenu(id("warp_scroll_selection"), (windowId, inventory, buf) -> {
            final var waystones = WaystoneImpl.readList(buf);
            return new WaystoneSelectionMenu(ModMenus.warpScrollSelection.get(), null, windowId, waystones, Collections.emptySet());
        });

        warpStoneSelection = menus.registerMenu(id("warp_stone_selection"), (windowId, inventory, buf) -> {
            final var waystones = WaystoneImpl.readList(buf);
            return new WaystoneSelectionMenu(ModMenus.warpStoneSelection.get(), null, windowId, waystones, Collections.emptySet());
        });

        portstoneSelection = menus.registerMenu(id("portstone_selection"), (windowId, inventory, buf) -> {
            final var waystones = WaystoneImpl.readList(buf);
            return new WaystoneSelectionMenu(ModMenus.waystoneSelection.get(), null, windowId, waystones, Collections.emptySet());
        });

        sharestoneSelection = menus.registerMenu(id("sharestone_selection"), (syncId, inventory, buf) -> {
            final var pos = buf.readBlockPos();
            final var waystones = WaystoneImpl.readList(buf);

            final var blockEntity = inventory.player.level().getBlockEntity(pos);
            if (blockEntity instanceof SharestoneBlockEntity sharestone) {
                return new WaystoneSelectionMenu(ModMenus.sharestoneSelection.get(),
                        sharestone.getWaystone(),
                        syncId,
                        waystones, Collections.emptySet());
            }

            return null;
        });

        inventorySelection = menus.registerMenu(id("inventory_selection"), (syncId, inventory, buf) -> {
            final var waystones = WaystoneImpl.readList(buf);
            return new WaystoneSelectionMenu(ModMenus.inventorySelection.get(), null, syncId, waystones, Set.of(TeleportFlags.INVENTORY_BUTTON));
        });

        adminSelection = menus.registerMenu(id("admin_selection"), (syncId, inventory, buf) -> {
            final var waystones = WaystoneImpl.readList(buf);
            return new WaystoneSelectionMenu(ModMenus.adminSelection.get(), null, syncId, waystones, Set.of(TeleportFlags.ADMIN));
        });

        warpPlate = menus.registerMenu(id("warp_plate"), (windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();

            BlockEntity blockEntity = inv.player.level().getBlockEntity(pos);
            if (blockEntity instanceof WarpPlateBlockEntity warpPlate) {
                return new WarpPlateMenu(windowId, warpPlate, warpPlate.getContainerData(), inv);
            }

            return null;
        });

        waystoneSettings = menus.registerMenu(id("waystone"), (windowId, inv, data) -> {
            final var pos = data.readBlockPos();
            final var waystone = WaystoneImpl.read(data);
            final var canEdit = data.readBoolean();

            BlockEntity blockEntity = inv.player.level().getBlockEntity(pos);
            if (blockEntity instanceof WaystoneBlockEntityBase waystoneBlockEntity) {
                return new WaystoneMenu(windowId, waystone, waystoneBlockEntity, waystoneBlockEntity.getContainerData(), inv, canEdit);
            }

            return null;

        });
    }

    @NotNull
    private static ResourceLocation id(String name) {
        return new ResourceLocation(Waystones.MOD_ID, name);
    }

}
