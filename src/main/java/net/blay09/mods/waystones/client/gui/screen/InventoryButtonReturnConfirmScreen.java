package net.blay09.mods.waystones.client.gui.screen;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.InventoryButtonMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class InventoryButtonReturnConfirmScreen extends ConfirmScreen {

    private final String waystoneName;

    public InventoryButtonReturnConfirmScreen() {
        this("");
    }

    public InventoryButtonReturnConfirmScreen(String targetWaystone) {
        super(result -> {
            if (result) {
                NetworkHandler.channel.sendToServer(new InventoryButtonMessage());
            }
            Minecraft.getInstance().displayGuiScreen(null);
        }, new TranslationTextComponent("gui.waystones:confirmReturn"), new StringTextComponent(""));

        this.waystoneName = getWaystoneName(targetWaystone);
    }

    private static String getWaystoneName(String targetWaystone) {
        if (!targetWaystone.isEmpty()) {
            return TextFormatting.GRAY + I18n.format("gui.waystones:confirmReturn.boundTo", targetWaystone);
        }

        IWaystone nearestWaystone = PlayerWaystoneManager.getNearestWaystone(Minecraft.getInstance().player);
        if (nearestWaystone != null) {
            return TextFormatting.GRAY + I18n.format("gui.waystones:confirmReturn.boundTo", nearestWaystone.getName());
        }

        return TextFormatting.GRAY + I18n.format("gui.waystones:confirmReturn.noWaystoneActive");
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
        drawCenteredString(fontRenderer, waystoneName, width / 2, 100, 0xFFFFFF);
    }
}
