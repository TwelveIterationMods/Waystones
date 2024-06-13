package net.blay09.mods.waystones.menu;

import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.menu.BalmMenuFactory;
import net.blay09.mods.balm.api.menu.BalmMenus;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.TeleportFlags;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.block.entity.WarpPlateBlockEntity;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntity;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.blay09.mods.waystones.core.WaystoneImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
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
    public static DeferredObject<MenuType<WaystoneModifierMenu>> waystoneModifiers;
    public static DeferredObject<MenuType<WaystoneEditMenu>> waystoneSettings;

    public static void initialize(BalmMenus menus) {
        waystoneSelection = menus.registerMenu(id("waystone_selection"), new BalmMenuFactory<WaystoneSelectionMenu, WaystoneSelectionMenu.Data>() {
            @Override
            public WaystoneSelectionMenu create(int windowId, Inventory inventory, WaystoneSelectionMenu.Data data) {
                final var blockEntity = inventory.player.level().getBlockEntity(data.pos());
                if (blockEntity instanceof WaystoneBlockEntity waystone) {
                    return new WaystoneSelectionMenu(ModMenus.waystoneSelection.get(),
                            waystone.getWaystone(),
                            windowId,
                            data.waystones(),
                            Collections.emptySet());
                }

                return null;
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, WaystoneSelectionMenu.Data> getStreamCodec() {
                return WaystoneSelectionMenu.STREAM_CODEC;
            }
        });

        warpScrollSelection = menus.registerMenu(id("warp_scroll_selection"), new BalmMenuFactory<WaystoneSelectionMenu, Collection<Waystone>>() {
            @Override
            public WaystoneSelectionMenu create(int windowId, Inventory inventory, Collection<Waystone> waystones) {
                return new WaystoneSelectionMenu(ModMenus.warpScrollSelection.get(), null, windowId, waystones, Collections.emptySet());
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, Collection<Waystone>> getStreamCodec() {
                return WaystoneImpl.LIST_STREAM_CODEC;
            }
        });

        warpStoneSelection = menus.registerMenu(id("warp_stone_selection"), new BalmMenuFactory<WaystoneSelectionMenu, Collection<Waystone>>() {
            @Override
            public WaystoneSelectionMenu create(int windowId, Inventory inventory, Collection<Waystone> waystones) {
                return new WaystoneSelectionMenu(ModMenus.warpStoneSelection.get(), null, windowId, waystones, Collections.emptySet());
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, Collection<Waystone>> getStreamCodec() {
                return WaystoneImpl.LIST_STREAM_CODEC;
            }
        });

        portstoneSelection = menus.registerMenu(id("portstone_selection"), new BalmMenuFactory<WaystoneSelectionMenu, Collection<Waystone>>() {
            @Override
            public WaystoneSelectionMenu create(int windowId, Inventory inventory, Collection<Waystone> waystones) {
                return new WaystoneSelectionMenu(ModMenus.portstoneSelection.get(), null, windowId, waystones, Collections.emptySet());
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, Collection<Waystone>> getStreamCodec() {
                return WaystoneImpl.LIST_STREAM_CODEC;
            }
        });

        sharestoneSelection = menus.registerMenu(id("sharestone_selection"), new BalmMenuFactory<WaystoneSelectionMenu, WaystoneSelectionMenu.Data>() {
            @Override
            public WaystoneSelectionMenu create(int windowId, Inventory inventory, WaystoneSelectionMenu.Data data) {
                final var blockEntity = inventory.player.level().getBlockEntity(data.pos());
                if (blockEntity instanceof WaystoneBlockEntity waystone) {
                    return new WaystoneSelectionMenu(ModMenus.sharestoneSelection.get(),
                            waystone.getWaystone(),
                            windowId,
                            data.waystones(),
                            Collections.emptySet());
                }

                return null;
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, WaystoneSelectionMenu.Data> getStreamCodec() {
                return WaystoneSelectionMenu.STREAM_CODEC;
            }
        });

        inventorySelection = menus.registerMenu(id("inventory_selection"), new BalmMenuFactory<WaystoneSelectionMenu, Collection<Waystone>>() {
            @Override
            public WaystoneSelectionMenu create(int windowId, Inventory inventory, Collection<Waystone> waystones) {
                return new WaystoneSelectionMenu(ModMenus.inventorySelection.get(), null, windowId, waystones, Set.of(TeleportFlags.INVENTORY_BUTTON));
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, Collection<Waystone>> getStreamCodec() {
                return WaystoneImpl.LIST_STREAM_CODEC;
            }
        });

        adminSelection = menus.registerMenu(id("admin_selection"), new BalmMenuFactory<WaystoneSelectionMenu, Collection<Waystone>>() {
            @Override
            public WaystoneSelectionMenu create(int windowId, Inventory inventory, Collection<Waystone> waystones) {
                return new WaystoneSelectionMenu(ModMenus.adminSelection.get(), null, windowId, waystones, Set.of(TeleportFlags.ADMIN));
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, Collection<Waystone>> getStreamCodec() {
                return WaystoneImpl.LIST_STREAM_CODEC;
            }
        });

        waystoneModifiers = menus.registerMenu(id("waystone_modifiers"), new BalmMenuFactory<WaystoneModifierMenu, BlockPos>() {
            @Override
            public WaystoneModifierMenu create(int windowId, Inventory inventory, BlockPos pos) {
                final var blockEntity = inventory.player.level().getBlockEntity(pos);
                if (blockEntity instanceof WaystoneBlockEntityBase waystoneBlockEntity) {
                    return new WaystoneModifierMenu(windowId, waystoneBlockEntity, inventory);
                }
                return null;
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, BlockPos> getStreamCodec() {
                return BlockPos.STREAM_CODEC.cast();
            }
        });

        waystoneSettings = menus.registerMenu(id("waystone"), new BalmMenuFactory<WaystoneEditMenu, WaystoneEditMenu.Data>() {
            @Override
            public WaystoneEditMenu create(int windowId, Inventory inventory, WaystoneEditMenu.Data data) {
                BlockEntity blockEntity = inventory.player.level().getBlockEntity(data.pos());
                if (blockEntity instanceof WaystoneBlockEntityBase waystoneBlockEntity) {
                    return new WaystoneEditMenu(windowId, data.waystone(), waystoneBlockEntity, inventory, data.modifierCount(), data.canEdit());
                }

                return null;
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, WaystoneEditMenu.Data> getStreamCodec() {
                return WaystoneEditMenu.STREAM_CODEC;
            }
        });
    }

    @NotNull
    private static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(Waystones.MOD_ID, name);
    }

}
