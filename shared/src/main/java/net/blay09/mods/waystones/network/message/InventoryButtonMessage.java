package net.blay09.mods.waystones.network.message;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.TeleportFlags;
import net.blay09.mods.waystones.api.WaystonesAPI;
import net.blay09.mods.waystones.config.InventoryButtonMode;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.core.WaystoneImpl;
import net.blay09.mods.waystones.menu.ModMenus;
import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.Set;

public class InventoryButtonMessage {

    public static void encode(InventoryButtonMessage message, FriendlyByteBuf buf) {
    }

    public static InventoryButtonMessage decode(FriendlyByteBuf buf) {
        return new InventoryButtonMessage();
    }

    public static void handle(final ServerPlayer player, InventoryButtonMessage message) {
        InventoryButtonMode inventoryButtonMode = WaystonesConfig.getActive().getInventoryButtonMode();
        if (!inventoryButtonMode.isEnabled()) {
            return;
        }

        if (player == null) {
            return;
        }

        // Reset cooldown if player is in creative mode
        if (player.getAbilities().instabuild) {
            PlayerWaystoneManager.resetCooldowns(player);
        }

        Waystone waystone = PlayerWaystoneManager.getInventoryButtonTarget(player);
        if (waystone != null) {
            WaystonesAPI.createDefaultTeleportContext(player, waystone, it -> it.addFlag(TeleportFlags.INVENTORY_BUTTON))
                    .mapLeft(WaystonesAPI::tryTeleport);
        } else if (inventoryButtonMode.isReturnToAny()) {
            final var waystones = PlayerWaystoneManager.getTargetsForInventoryButton(player);
            PlayerWaystoneManager.ensureSortingIndex(player, waystones);
            final BalmMenuProvider containerProvider = new BalmMenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("container.waystones.waystone_selection");
                }

                @Override
                public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
                    return new WaystoneSelectionMenu(ModMenus.inventorySelection.get(), null, windowId, waystones, Set.of(TeleportFlags.INVENTORY_BUTTON));
                }

                @Override
                public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                    WaystoneImpl.writeList(buf, waystones);
                }
            };
            Balm.getNetworking().openGui(player, containerProvider);
        }
    }

}
