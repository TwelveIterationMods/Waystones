package net.blay09.mods.waystones.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.blay09.mods.forbic.network.ForbicNetworking;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.network.message.InventoryButtonMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class InventoryButtonReturnConfirmScreen extends ConfirmScreen {

    private final String waystoneName;

    public InventoryButtonReturnConfirmScreen() {
        this("");
    }

    public InventoryButtonReturnConfirmScreen(String targetWaystone) {
        super(result -> {
            if (result) {
                ForbicNetworking.sendToServer(new InventoryButtonMessage());
            }
            Minecraft.getInstance().setScreen(null);
        }, new TranslatableComponent("gui.waystones.inventory.confirm_return"), new TextComponent(""));

        this.waystoneName = getWaystoneName(targetWaystone);
    }

    private static String getWaystoneName(String targetWaystone) {
        if (!targetWaystone.isEmpty()) {
            return ChatFormatting.GRAY + I18n.get("gui.waystones.inventory.confirm_return_bound_to", targetWaystone);
        }

        IWaystone nearestWaystone = PlayerWaystoneManager.getNearestWaystone(Minecraft.getInstance().player);
        if (nearestWaystone != null) {
            return ChatFormatting.GRAY + I18n.get("gui.waystones.inventory.confirm_return_bound_to", nearestWaystone.getName());
        }

        return ChatFormatting.GRAY + I18n.get("gui.waystones.inventory.confirm_return.noWaystoneActive");
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        Font font = Minecraft.getInstance().font;
        drawCenteredString(matrixStack, font, waystoneName, width / 2, 100, 0xFFFFFF);
    }
}
