package net.blay09.mods.waystones.client;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.client.gui.screen.InventoryButtonReturnConfirmScreen;
import net.blay09.mods.waystones.client.gui.widget.WaystoneInventoryButton;
import net.blay09.mods.waystones.config.InventoryButtonMode;
import net.blay09.mods.waystones.config.WaystoneConfig;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.InventoryButtonMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Waystones.MOD_ID, value = Dist.CLIENT)
public class InventoryButtonGuiHandler {

    private static WaystoneInventoryButton buttonWarp;

    @SubscribeEvent
    public static void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
        if (!(event.getGui() instanceof InventoryScreen)) {
            return;
        }

        InventoryButtonMode inventoryButtonMode = WaystoneConfig.getInventoryButtonMode();
        if (!inventoryButtonMode.isEnabled()) {
            return;
        }

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
        });
        event.addWidget(buttonWarp);
    }

    @SubscribeEvent
    public static void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
        // Render the inventory button tooltip when it's hovered
        if (event.getGui() instanceof InventoryScreen && buttonWarp != null && buttonWarp.isHovered()) {
            InventoryButtonMode inventoryButtonMode = WaystoneConfig.getInventoryButtonMode();
            List<ITextProperties> tooltip = new ArrayList<>();
            long timeLeft = PlayerWaystoneManager.getInventoryButtonCooldownLeft(Minecraft.getInstance().player);
            int secondsLeft = (int) (timeLeft / 1000);
            if (inventoryButtonMode.hasNamedTarget()) {
                tooltip.add(formatTranslation(TextFormatting.YELLOW, "gui.waystones.inventory.return_to_waystone"));
                tooltip.add(formatTranslation(TextFormatting.GRAY, "tooltip.waystones.bound_to", TextFormatting.DARK_AQUA + inventoryButtonMode.getNamedTarget()));
                if (secondsLeft > 0) {
                    tooltip.add(new StringTextComponent(""));
                }
            } else if (inventoryButtonMode.isReturnToNearest()) {
                tooltip.add(formatTranslation(TextFormatting.YELLOW, "gui.waystones.inventory.return_to_nearest_waystone"));
                IWaystone nearestWaystone = PlayerWaystoneManager.getNearestWaystone(Minecraft.getInstance().player);
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
                if (PlayerWaystoneManager.getWaystones(Minecraft.getInstance().player).isEmpty()) {
                    tooltip.add(formatTranslation(TextFormatting.RED, "gui.waystones.inventory.no_waystones_activated"));
                }
            }

            if (secondsLeft > 0) {
                tooltip.add(formatTranslation(TextFormatting.GOLD, "tooltip.waystones.cooldown_left", secondsLeft));
            }

            event.getGui().renderTooltip(event.getMatrixStack(), tooltip, event.getMouseX(), event.getMouseY());
        }
    }

    private static ITextComponent formatTranslation(TextFormatting formatting, String key, Object... args) {
        final TranslationTextComponent result = new TranslationTextComponent(key, args);
        result.getStyle().setFormatting(formatting);
        return result;
    }

}
