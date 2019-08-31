package net.blay09.mods.waystones.client;

import com.google.common.collect.Lists;
import net.blay09.mods.waystones.*;
import net.blay09.mods.waystones.tileentity.WaystoneTileEntity;
import net.blay09.mods.waystones.client.gui.GuiButtonInventoryWarp;
import net.blay09.mods.waystones.client.gui.GuiConfirmInventoryButtonReturn;
import net.blay09.mods.waystones.client.gui.GuiEditWaystone;
import net.blay09.mods.waystones.client.gui.GuiWaystoneList;
import net.blay09.mods.waystones.client.render.RenderWaystone;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public class ClientProxy extends CommonProxy {

    private GuiButtonInventoryWarp buttonWarp;
    private boolean isVivecraftInstalled;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        MinecraftForge.EVENT_BUS.register(this);

        ClientRegistry.bindTileEntitySpecialRenderer(WaystoneTileEntity.class, new RenderWaystone());

        isVivecraftInstalled = ClientBrandRetriever.getClientModName().toLowerCase(Locale.ENGLISH).contains("vivecraft");
    }

    @SubscribeEvent
    public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
        if (WaystoneConfig.general.teleportButton && event.getGui() instanceof GuiInventory) {
            buttonWarp = new GuiButtonInventoryWarp((GuiContainer) event.getGui());
            event.getButtonList().add(buttonWarp);
        }
    }

    @SubscribeEvent
    public void onActionPerformed(GuiScreenEvent.ActionPerformedEvent.Pre event) {
        if (event.getButton() instanceof GuiButtonInventoryWarp) {
            PlayerEntity PlayerEntity = Minecraft.getInstance().player;
            if (PlayerWaystoneHelper.canFreeWarp(PlayerEntity)) {
                if (!WaystoneConfig.general.teleportButtonTarget.isEmpty()) {
                    event.getGui().mc.displayGuiScreen(new GuiConfirmInventoryButtonReturn(WaystoneConfig.general.teleportButtonTarget));
                } else if (WaystoneConfig.general.teleportButtonReturnOnly) {
                    if (PlayerWaystoneHelper.getLastWaystone(PlayerEntity) != null) {
                        event.getGui().mc.displayGuiScreen(new GuiConfirmInventoryButtonReturn());
                    }
                } else {
                    Waystones.proxy.openWaystoneSelection(PlayerEntity, WarpMode.INVENTORY_BUTTON, Hand.MAIN_HAND, null);
                }
            } else {
                event.getGui().mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 0.5f));
                event.setCanceled(true);
            }
        }
    }

    private static final List<String> tmpTooltip = Lists.newArrayList();

    @SubscribeEvent
    public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (event.getGui() instanceof InventoryScreen && buttonWarp != null && buttonWarp.isHovered()) {
            tmpTooltip.clear();
            long timeSince = System.currentTimeMillis() - PlayerWaystoneHelper.getLastFreeWarp(Minecraft.getInstance().player);
            int secondsLeft = (int) ((WaystoneConfig.general.teleportButtonCooldown * 1000 - timeSince) / 1000);
            if (!WaystoneConfig.general.teleportButtonTarget.isEmpty()) {
                tmpTooltip.add(TextFormatting.YELLOW + I18n.format("tooltip.waystones:returnToWaystone"));
                tmpTooltip.add(TextFormatting.GRAY + I18n.format("tooltip.waystones:boundTo", TextFormatting.DARK_AQUA + WaystoneConfig.general.teleportButtonTarget));
                if (secondsLeft > 0) {
                    tmpTooltip.add("");
                }
            } else if (WaystoneConfig.general.teleportButtonReturnOnly) {
                tmpTooltip.add(TextFormatting.YELLOW + I18n.format("tooltip.waystones:returnToWaystone"));
                WaystoneEntry lastEntry = PlayerWaystoneHelper.getLastWaystone(Minecraft.getInstance().player);
                if (lastEntry != null) {
                    tmpTooltip.add(TextFormatting.GRAY + I18n.format("tooltip.waystones:boundTo", TextFormatting.DARK_AQUA + lastEntry.getName()));
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
        if (!event.getEntity().getActiveItemStack().isEmpty() && event.getEntity().getActiveItemStack().getItem() == Waystones.itemReturnScroll) {
            event.setNewfov(event.getEntity().getItemInUseCount() / 64f * 2f + 0.5f);
        }
    }

    @Override
    public void openWaystoneSelection(PlayerEntity player, WarpMode mode, Hand hand, @Nullable WaystoneEntry fromWaystone) {
        if (player == Minecraft.getInstance().player) {
            WaystoneEntry[] waystones = PlayerWaystoneData.fromPlayer(Minecraft.getInstance().player).getWaystones();
            Minecraft.getInstance().displayGuiScreen(new GuiWaystoneList(waystones, mode, hand, fromWaystone));
        }
    }

    @Override
    public void openWaystoneSettings(PlayerEntity player, WaystoneEntry waystone, boolean fromSelectionGui) {
        if (player == Minecraft.getInstance().player) {
            Minecraft.getInstance().displayGuiScreen(new GuiEditWaystone(waystone, fromSelectionGui));
        }
    }

    @Override
    public void playSound(SoundEvent sound, BlockPos pos, float pitch) {
        Minecraft.getInstance().getSoundHandler().playSound(new PositionedSoundRecord(sound, SoundCategory.AMBIENT, WaystoneConfig.client.soundVolume, pitch, pos));
    }

    @Override
    public boolean isVivecraftInstalled() {
        return isVivecraftInstalled;
    }
}
