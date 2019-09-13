package net.blay09.mods.waystones.client.gui.screen;

import net.blay09.mods.waystones.PlayerWaystoneHelper;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.MessageFreeWarpReturn;
import net.blay09.mods.waystones.network.message.MessageTeleportToGlobal;
import net.blay09.mods.waystones.util.WaystoneEntry;
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
                if (targetWaystone.isEmpty()) {
                    NetworkHandler.channel.sendToServer(new MessageFreeWarpReturn());
                } else {
                    NetworkHandler.channel.sendToServer(new MessageTeleportToGlobal(targetWaystone));
                }
            }
            Minecraft.getInstance().displayGuiScreen(null);
        }, new TranslationTextComponent("gui.waystones:confirmReturn"), new StringTextComponent(""));

        this.waystoneName = getWaystoneName(targetWaystone);
    }

    private static String getWaystoneName(String targetWaystone) {
        if (!targetWaystone.isEmpty()) {
            return TextFormatting.GRAY + I18n.format("gui.waystones:confirmReturn.boundTo", targetWaystone);
        }

        WaystoneEntry lastEntry = PlayerWaystoneHelper.getLastWaystone(Minecraft.getInstance().player);
        if (lastEntry != null) {
            return TextFormatting.GRAY + I18n.format("gui.waystones:confirmReturn.boundTo", lastEntry.getName());
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
