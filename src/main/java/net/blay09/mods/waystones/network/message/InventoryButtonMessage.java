package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.config.InventoryButtonMode;
import net.blay09.mods.waystones.config.WaystoneConfig;
import net.blay09.mods.waystones.container.WaystoneSelectionContainer;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WarpMode;
import net.blay09.mods.waystones.core.WaystoneManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.Optional;
import java.util.function.Supplier;

public class InventoryButtonMessage {

    private static final INamedContainerProvider containerProvider = new INamedContainerProvider() {
        @Override
        public ITextComponent getDisplayName() {
            return new TranslationTextComponent("container.waystones.waystone_selection");
        }

        @Override
        public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
            return new WaystoneSelectionContainer(i, WarpMode.INVENTORY_BUTTON, null);
        }
    };

    public static void encode(InventoryButtonMessage message, PacketBuffer buf) {
    }

    public static InventoryButtonMessage decode(PacketBuffer buf) {
        return new InventoryButtonMessage();
    }

    public static void handle(InventoryButtonMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            InventoryButtonMode inventoryButtonMode = WaystoneConfig.getInventoryButtonMode();
            if (!inventoryButtonMode.isEnabled()) {
                return;
            }

            ServerPlayerEntity player = context.getSender();
            if (player == null) {
                return;
            }

            if (!PlayerWaystoneManager.canUseInventoryButton(player)) {
                return;
            }

            if (inventoryButtonMode.isReturnToNearest()) {
                IWaystone nearestWaystone = PlayerWaystoneManager.getNearestWaystone(player);
                if (nearestWaystone != null) {
                    PlayerWaystoneManager.tryTeleportToWaystone(player, nearestWaystone, WarpMode.INVENTORY_BUTTON, null);
                }
            } else if (inventoryButtonMode.isReturnToAny()) {
                NetworkHooks.openGui(player, containerProvider);
            } else if (inventoryButtonMode.hasNamedTarget()) {
                Optional<IWaystone> waystone = WaystoneManager.get().findWaystoneByName(inventoryButtonMode.getNamedTarget());
                waystone.ifPresent(iWaystone -> PlayerWaystoneManager.tryTeleportToWaystone(player, iWaystone, WarpMode.INVENTORY_BUTTON, null));
            }
        });
        context.setPacketHandled(true);
    }

}
