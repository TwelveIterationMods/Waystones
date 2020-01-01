package net.blay09.mods.waystones.client;

import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.client.gui.screen.InventoryButtonReturnConfirmScreen;
import net.blay09.mods.waystones.client.gui.widget.InventoryWarpButton;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Waystones.MOD_ID, value = Dist.CLIENT)
public class InventoryButtonGuiHandler {

    private static InventoryWarpButton buttonWarp;

    @SubscribeEvent
    public static void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
        if (WaystoneConfig.SERVER.teleportButton.get() && event.getGui() instanceof InventoryScreen) {
            buttonWarp = new InventoryWarpButton((ContainerScreen<?>) event.getGui(), button -> {
                PlayerEntity player = Minecraft.getInstance().player;
                Minecraft minecraft = event.getGui().getMinecraft();
                if (PlayerWaystoneManager.canUseInventoryButton(player)) {
                    if (!WaystoneConfig.COMMON.teleportButtonTarget.get().isEmpty()) {
                        minecraft.displayGuiScreen(new InventoryButtonReturnConfirmScreen(WaystoneConfig.COMMON.teleportButtonTarget.get()));
                    } else if (WaystoneConfig.SERVER.teleportButtonReturnOnly.get()) {
                        if (PlayerWaystoneManager.getNearestWaystone(player) != null) {
                            minecraft.displayGuiScreen(new InventoryButtonReturnConfirmScreen());
                        }
                    } else {
                        // TODO Waystones.proxy.openWaystoneSelection(player, WarpMode.INVENTORY_BUTTON, Hand.MAIN_HAND, null);
                    }
                } else {
                    minecraft.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 0.5f));
                    event.setCanceled(true);
                }
            });
            event.addWidget(buttonWarp);
        }
    }

    @SubscribeEvent
    public static void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (event.getGui() instanceof InventoryScreen && buttonWarp != null && buttonWarp.isHovered()) {
            List<String> tooltip = new ArrayList<>();
            long timeSince = System.currentTimeMillis() - PlayerWaystoneManager.getLastInventoryWarp(Minecraft.getInstance().player);
            int secondsLeft = (int) ((WaystoneConfig.SERVER.teleportButtonCooldown.get() * 1000 - timeSince) / 1000);
            if (!WaystoneConfig.COMMON.teleportButtonTarget.get().isEmpty()) {
                tooltip.add(TextFormatting.YELLOW + I18n.format("tooltip.waystones:returnToWaystone"));
                tooltip.add(TextFormatting.GRAY + I18n.format("tooltip.waystones:boundTo", TextFormatting.DARK_AQUA + WaystoneConfig.COMMON.teleportButtonTarget.get()));
                if (secondsLeft > 0) {
                    tooltip.add("");
                }
            } else if (WaystoneConfig.SERVER.teleportButtonReturnOnly.get()) {
                tooltip.add(TextFormatting.YELLOW + I18n.format("tooltip.waystones:returnToWaystone"));
                IWaystone nearestWaystone = PlayerWaystoneManager.getNearestWaystone(Minecraft.getInstance().player);
                if (nearestWaystone != null) {
                    tooltip.add(TextFormatting.GRAY + I18n.format("tooltip.waystones:boundTo", TextFormatting.DARK_AQUA + nearestWaystone.getName()));
                } else {
                    tooltip.add(TextFormatting.GRAY + I18n.format("tooltip.waystones:boundTo", I18n.format("tooltip.waystones:none")));
                }
                if (secondsLeft > 0) {
                    tooltip.add("");
                }
            } else {
                tooltip.add(TextFormatting.YELLOW + I18n.format("tooltip.waystones:openWaystoneMenu"));
            }
            if (secondsLeft > 0) {
                tooltip.add(TextFormatting.GRAY + I18n.format("tooltip.waystones:cooldownLeft", secondsLeft));
            }
            event.getGui().renderTooltip(tooltip, event.getMouseX(), event.getMouseY());
        }
    }

}
