package net.blay09.mods.waystones.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class SortWaystoneButton extends Button {

    private static final ResourceLocation SERVER_SELECTION_BUTTONS = new ResourceLocation("textures/gui/server_selection.png");
    private final int sortDir;
    private final int visibleRegionStart;
    private final int visibleRegionHeight;

    public SortWaystoneButton(int x, int y, int sortDir, int visibleRegionStart, int visibleRegionHeight, OnPress pressable) {
        super(x, y, 11, 7, Component.empty(), pressable, Button.DEFAULT_NARRATION);
        this.sortDir = sortDir;
        this.visibleRegionStart = visibleRegionStart;
        this.visibleRegionHeight = visibleRegionHeight;
    }

    @Override
    public void renderWidget(PoseStack poseStack, int mouseX, int mouseY, float partial) {
        if (mouseY >= visibleRegionStart && mouseY < visibleRegionStart + visibleRegionHeight) {
            RenderSystem.setShaderTexture(0, SERVER_SELECTION_BUTTONS);
            this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
            int renderY = getY() - (sortDir == 1 ? 20 : 5);
            RenderSystem.enableBlend();
            if (active && isHovered) {
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            } else if (active) {
                RenderSystem.setShaderColor(1f, 1f, 1f, 0.75f);
            } else {
                RenderSystem.setShaderColor(1f, 1f, 1f, 0.25f);
            }

            if (isHovered && active) {
                blit(poseStack, getX() - 5, renderY, sortDir == 1 ? 64 : 96, 32, 32, 32);
            } else {
                blit(poseStack, getX() - 5, renderY, sortDir == 1 ? 64 : 96, 0, 32, 32);
            }

            RenderSystem.disableBlend();
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        }
    }

}
