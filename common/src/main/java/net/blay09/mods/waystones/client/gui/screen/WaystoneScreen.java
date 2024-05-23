package net.blay09.mods.waystones.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.client.gui.widget.ITooltipProvider;
import net.blay09.mods.waystones.client.gui.widget.WaystoneVisbilityButton;
import net.blay09.mods.waystones.core.WaystoneVisibilities;
import net.blay09.mods.waystones.menu.WaystoneMenu;
import net.blay09.mods.waystones.network.message.EditWaystoneMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WaystoneScreen extends AbstractContainerScreen<WaystoneMenu> {

    private static final ResourceLocation WAYSTONE_GUI_TEXTURES = new ResourceLocation(Waystones.MOD_ID, "textures/gui/menu/waystone.png");

    private final List<ITooltipProvider> tooltipProviders = new ArrayList<>();

    private EditBox textField;
    private WaystoneVisbilityButton visibilityButton;

    public WaystoneScreen(WaystoneMenu container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        imageHeight = 210;
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    public void init() {
        super.init();
        final var waystone = menu.getWaystone();
        var oldText = waystone.getName().getString();
        if (textField != null) {
            oldText = textField.getValue();
        }
        var oldVisibility = waystone.getVisibility();
        if (visibilityButton != null) {
            oldVisibility = visibilityButton.getVisibility();
        }

        tooltipProviders.clear();

        textField = new EditBox(Minecraft.getInstance().font, leftPos + 33, topPos + 9, 110, 16, textField, Component.empty());
        textField.setMaxLength(128);
        textField.setValue(oldText);
        textField.setEditable(menu.canEdit());
        addRenderableWidget(textField);
        if (menu.canEdit() && oldText.isEmpty()) {
            setInitialFocus(textField);
        }

        final var visibilityOptions = WaystoneVisibilities.getVisibilityOptions(Minecraft.getInstance().player, waystone);
        visibilityButton = new WaystoneVisbilityButton(leftPos + 9, topPos + 8, oldVisibility, visibilityOptions, menu.canEdit());
        addRenderableWidget(visibilityButton);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (menu.canEdit() && textField.isMouseOver(mouseX, mouseY) && button == 1) {
            textField.setValue("");
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (textField.keyPressed(keyCode, scanCode, modifiers) || textField.isFocused()) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_ENTER) {
                this.onClose();
            }

            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        renderTooltip(guiGraphics, mouseX, mouseY);
        for (ITooltipProvider tooltipProvider : tooltipProviders) {
            if (tooltipProvider.shouldShowTooltip()) {
                guiGraphics.renderTooltip(Minecraft.getInstance().font, tooltipProvider.getTooltipComponents(), Optional.empty(), mouseX, mouseY);
            }
        }

        if (textField != null && textField.getValue().isEmpty()) {
            guiGraphics.drawString(Minecraft.getInstance().font,
                    Component.translatable("waystones.untitled_waystone"),
                    textField.getX() + 4,
                    textField.getY() + 4,
                    0x808080);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        guiGraphics.setColor(1f, 1f, 1f, 1f);
        guiGraphics.blit(WAYSTONE_GUI_TEXTURES, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        guiGraphics.blit(WAYSTONE_GUI_TEXTURES, leftPos + 86, topPos + 53, 176, 4, 4, (int) (10 * menu.getAttunementProgress()));
        guiGraphics.blit(WAYSTONE_GUI_TEXTURES, leftPos + 107 - (int) (10 * menu.getAttunementProgress()), topPos + 70, 176, 0, (int) (10 * menu.getAttunementProgress()), 4);
        guiGraphics.blit(WAYSTONE_GUI_TEXTURES, leftPos + 86, topPos + 91 - (int) (10 * menu.getAttunementProgress()), 176, 4, 4, (int) (10 * menu.getAttunementProgress()));
        guiGraphics.blit(WAYSTONE_GUI_TEXTURES, leftPos + 69, topPos + 70, 176, 0, (int) (10 * menu.getAttunementProgress()), 4);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0x404040, false);
    }

    @Override
    public void onClose() {
        if (textField != null && visibilityButton != null) {
            Balm.getNetworking()
                    .sendToServer(new EditWaystoneMessage(menu.getWaystone().getWaystoneUid(), textField.getValue(), visibilityButton.getVisibility()));
        }

        super.onClose();
    }

    @Override
    protected <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(T widget) {
        if (widget instanceof ITooltipProvider) {
            tooltipProviders.add((ITooltipProvider) widget);
        }
        return super.addRenderableWidget(widget);
    }
}
