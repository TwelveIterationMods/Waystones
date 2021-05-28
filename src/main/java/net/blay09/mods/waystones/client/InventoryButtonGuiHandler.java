package net.blay09.mods.waystones.client;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.client.gui.screen.InventoryButtonReturnConfirmScreen;
import net.blay09.mods.waystones.client.gui.widget.WaystoneInventoryButton;
import net.blay09.mods.waystones.config.InventoryButtonMode;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WarpMode;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.InventoryButtonMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Waystones.MOD_ID, value = Dist.CLIENT)
public class InventoryButtonGuiHandler {

    private static WaystoneInventoryButton buttonWarp;

    @SubscribeEvent
    public static void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
        if (!(event.getGui() instanceof InventoryScreen) && !(event.getGui() instanceof CreativeScreen)) {
            return;
        }

        if (event.getGui() != Minecraft.getInstance().currentScreen) {
            return;
        }

        InventoryButtonMode inventoryButtonMode = WaystonesConfig.getInventoryButtonMode();
        if (!inventoryButtonMode.isEnabled()) {
            return;
        }

        Supplier<Integer> xPosition = event.getGui() instanceof CreativeScreen ? WaystonesConfig.CLIENT.creativeWarpButtonX::get : WaystonesConfig.CLIENT.teleportButtonX::get;
        Supplier<Integer> yPosition = event.getGui() instanceof CreativeScreen ? WaystonesConfig.CLIENT.creativeWarpButtonY::get : WaystonesConfig.CLIENT.teleportButtonY::get;
        buttonWarp = new WaystoneInventoryButton((ContainerScreen<?>) event.getGui(), button -> {
            Minecraft mc = event.getGui().getMinecraft();
            PlayerEntity player = mc.player;

            // Reset cooldown if player is in creative mode
            if (player.abilities.isCreativeMode) {
                PlayerWaystoneManager.setInventoryButtonCooldownUntil(player, 0);
            }

            if (PlayerWaystoneManager.canUseInventoryButton(player)) {
                if (inventoryButtonMode.hasNamedTarget()) {
                    mc.displayGuiScreen(new InventoryButtonReturnConfirmScreen(inventoryButtonMode.getNamedTarget()));
                } else if (inventoryButtonMode.isReturnToNearest()) {
                    if (PlayerWaystoneManager.getNearestWaystone(player) != null) {
                        mc.displayGuiScreen(new InventoryButtonReturnConfirmScreen());
                    }
                } else if (inventoryButtonMode.isReturnToAny()) {
                    NetworkHandler.channel.sendToServer(new InventoryButtonMessage());
                }
            } else {
                mc.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 0.5f));
            }
        }, () -> {
            if (event.getGui() instanceof CreativeScreen) {
                CreativeScreen gui = ((CreativeScreen) event.getGui());
                return gui.getSelectedTabIndex() == ItemGroup.INVENTORY.getIndex();
            }

            return true;
        }, xPosition, yPosition);
        event.addWidget(buttonWarp);
    }

    @SubscribeEvent
    public static void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
        // Render the inventory button tooltip when it's hovered
        if ((event.getGui() instanceof InventoryScreen || event.getGui() instanceof CreativeScreen) && buttonWarp != null && buttonWarp.isHovered()) {
            InventoryButtonMode inventoryButtonMode = WaystonesConfig.getInventoryButtonMode();
            List<ITextComponent> tooltip = new ArrayList<>();
            ClientPlayerEntity player = Minecraft.getInstance().player;
            long timeLeft = PlayerWaystoneManager.getInventoryButtonCooldownLeft(player);
            IWaystone waystone = PlayerWaystoneManager.getInventoryButtonWaystone(player);
            int xpLevelCost = waystone != null ? PlayerWaystoneManager.getExperienceLevelCost(player, waystone, WarpMode.INVENTORY_BUTTON, (IWaystone) null) : 0;
            int secondsLeft = (int) (timeLeft / 20);
            if (inventoryButtonMode.hasNamedTarget()) {
                tooltip.add(formatTranslation(TextFormatting.YELLOW, "gui.waystones.inventory.return_to_waystone"));
                tooltip.add(formatTranslation(TextFormatting.GRAY, "tooltip.waystones.bound_to", TextFormatting.DARK_AQUA + inventoryButtonMode.getNamedTarget()));
                if (secondsLeft > 0) {
                    tooltip.add(new StringTextComponent(""));
                }
            } else if (inventoryButtonMode.isReturnToNearest()) {
                tooltip.add(formatTranslation(TextFormatting.YELLOW, "gui.waystones.inventory.return_to_nearest_waystone"));
                IWaystone nearestWaystone = PlayerWaystoneManager.getNearestWaystone(player);
                if (nearestWaystone != null) {
                    tooltip.add(formatTranslation(TextFormatting.GRAY, "tooltip.waystones.bound_to", TextFormatting.DARK_AQUA + nearestWaystone.getName()));
                } else {
                    tooltip.add(formatTranslation(TextFormatting.RED, "gui.waystones.inventory.no_waystones_activated"));
                }
                if (secondsLeft > 0) {
                    tooltip.add(new StringTextComponent(""));
                }
            } else if (inventoryButtonMode.isReturnToAny()) {
                tooltip.add(formatTranslation(TextFormatting.YELLOW, "gui.waystones.inventory.return_to_waystone"));
                if (PlayerWaystoneManager.getWaystones(player).isEmpty()) {
                    tooltip.add(formatTranslation(TextFormatting.RED, "gui.waystones.inventory.no_waystones_activated"));
                }
            }

            if (xpLevelCost > 0 && player.experienceLevel < xpLevelCost) {
                tooltip.add(formatTranslation(TextFormatting.RED, "tooltip.waystones.not_enough_xp", xpLevelCost));
            }

            if (secondsLeft > 0) {
                tooltip.add(formatTranslation(TextFormatting.GOLD, "tooltip.waystones.cooldown_left", secondsLeft));
            }

            event.getGui().func_243308_b(event.getMatrixStack(), tooltip, event.getMouseX(), event.getMouseY()); // renderTooltip
        }
    }

    private static ITextComponent formatTranslation(TextFormatting formatting, String key, Object... args) {
        final TranslationTextComponent result = new TranslationTextComponent(key, args);
        result.mergeStyle(formatting);
        return result;
    }

}
