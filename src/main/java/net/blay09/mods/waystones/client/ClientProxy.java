package net.blay09.mods.waystones.client;

import com.google.common.collect.Lists;
import net.blay09.mods.waystones.*;
import net.blay09.mods.waystones.client.gui.screen.EditWaystoneScreen;
import net.blay09.mods.waystones.client.gui.screen.InventoryButtonReturnConfirmScreen;
import net.blay09.mods.waystones.client.gui.screen.WaystoneListScreen;
import net.blay09.mods.waystones.client.gui.widget.InventoryWarpButton;
import net.blay09.mods.waystones.core.IWaystone;
import net.blay09.mods.waystones.item.ModItems;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public class ClientProxy extends CommonProxy {

    private InventoryWarpButton buttonWarp;
    private boolean isVivecraftInstalled;

    public ClientProxy() {
        isVivecraftInstalled = ClientBrandRetriever.getClientModName().toLowerCase(Locale.ENGLISH).contains("vivecraft");
    }

    @SubscribeEvent
    public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
        if (WaystoneConfig.SERVER.teleportButton.get() && event.getGui() instanceof InventoryScreen) {
            buttonWarp = new InventoryWarpButton((ContainerScreen<?>) event.getGui(), button -> {
                PlayerEntity PlayerEntity = Minecraft.getInstance().player;
                Minecraft minecraft = event.getGui().getMinecraft();
                if (PlayerWaystoneHelper.canFreeWarp(PlayerEntity)) {
                    if (!WaystoneConfig.COMMON.teleportButtonTarget.get().isEmpty()) {
                        minecraft.displayGuiScreen(new InventoryButtonReturnConfirmScreen(WaystoneConfig.COMMON.teleportButtonTarget.get()));
                    } else if (WaystoneConfig.SERVER.teleportButtonReturnOnly.get()) {
                        if (PlayerWaystoneHelper.getNearestWaystone(PlayerEntity) != null) {
                            minecraft.displayGuiScreen(new InventoryButtonReturnConfirmScreen());
                        }
                    } else {
                        Waystones.proxy.openWaystoneSelection(PlayerEntity, WarpMode.INVENTORY_BUTTON, Hand.MAIN_HAND, null);
                    }
                } else {
                    minecraft.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 0.5f));
                    event.setCanceled(true);
                }
            });
            event.addWidget(buttonWarp);
        }
    }

    private static final List<String> tmpTooltip = Lists.newArrayList();

    @SubscribeEvent
    public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (event.getGui() instanceof InventoryScreen && buttonWarp != null && buttonWarp.isHovered()) {
            tmpTooltip.clear();
            long timeSince = System.currentTimeMillis() - PlayerWaystoneHelper.getLastFreeWarp(Minecraft.getInstance().player);
            int secondsLeft = (int) ((WaystoneConfig.SERVER.teleportButtonCooldown.get() * 1000 - timeSince) / 1000);
            if (!WaystoneConfig.COMMON.teleportButtonTarget.get().isEmpty()) {
                tmpTooltip.add(TextFormatting.YELLOW + I18n.format("tooltip.waystones:returnToWaystone"));
                tmpTooltip.add(TextFormatting.GRAY + I18n.format("tooltip.waystones:boundTo", TextFormatting.DARK_AQUA + WaystoneConfig.COMMON.teleportButtonTarget.get()));
                if (secondsLeft > 0) {
                    tmpTooltip.add("");
                }
            } else if (WaystoneConfig.SERVER.teleportButtonReturnOnly.get()) {
                tmpTooltip.add(TextFormatting.YELLOW + I18n.format("tooltip.waystones:returnToWaystone"));
                IWaystone nearestWaystone = PlayerWaystoneHelper.getNearestWaystone(Minecraft.getInstance().player);
                if (nearestWaystone != null) {
                    tmpTooltip.add(TextFormatting.GRAY + I18n.format("tooltip.waystones:boundTo", TextFormatting.DARK_AQUA + nearestWaystone.getName()));
                } else {
                    tmpTooltip.add(TextFormatting.GRAY + I18n.format("tooltip.waystones:boundTo", I18n.format("tooltip.waystones:none")));
                }
                if (secondsLeft > 0) {
                    tmpTooltip.add("");
                }
            } else {
                tmpTooltip.add(TextFormatting.YELLOW + I18n.format("tooltip.waystones:openWaystoneMenu"));
            }
            if (secondsLeft > 0) {
                tmpTooltip.add(TextFormatting.GRAY + I18n.format("tooltip.waystones:cooldownLeft", secondsLeft));
            }
            event.getGui().renderTooltip(tmpTooltip, event.getMouseX(), event.getMouseY());
        }
    }

    @SubscribeEvent
    public void onFOV(FOVUpdateEvent event) {
        if (!event.getEntity().getActiveItemStack().isEmpty() && event.getEntity().getActiveItemStack().getItem() == ModItems.returnScroll) {
            event.setNewfov(event.getEntity().getItemInUseCount() / 64f * 2f + 0.5f);
        }
    }

    @Override
    public void openWaystoneSelection(PlayerEntity player, WarpMode mode, Hand hand, @Nullable IWaystone fromWaystone) {
        if (player == Minecraft.getInstance().player) {
            IWaystone[] waystones = PlayerWaystoneData.fromPlayer(Minecraft.getInstance().player).getWaystones();
            Minecraft.getInstance().displayGuiScreen(new WaystoneListScreen(waystones, mode, hand, fromWaystone));
        }
    }

    @Override
    public void openWaystoneSettings(PlayerEntity player, IWaystone waystone, boolean fromSelectionGui) {
        if (player == Minecraft.getInstance().player) {
            Minecraft.getInstance().displayGuiScreen(new EditWaystoneScreen(waystone, fromSelectionGui));
        }
    }

    @Override
    public void playSound(SoundEvent sound, BlockPos pos, float pitch) {
        Minecraft.getInstance().getSoundHandler().play(new SimpleSound(sound, SoundCategory.AMBIENT, WaystoneConfig.CLIENT.soundVolume.get().floatValue(), pitch, pos));
    }

    @Override
    public boolean isVivecraftInstalled() {
        return isVivecraftInstalled;
    }
}
