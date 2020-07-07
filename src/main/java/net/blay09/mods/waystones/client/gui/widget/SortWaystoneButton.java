package net.blay09.mods.waystones.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

public class SortWaystoneButton extends Button {

    private static final ResourceLocation SERVER_SELECTION_BUTTONS = new ResourceLocation("textures/gui/server_selection.png");
    private final int sortDir;
    private final int visibleRegionStart;
    private final int visibleRegionHeight;

    public SortWaystoneButton(int x, int y, int sortDir, int visibleRegionStart, int visibleRegionHeight, IPressable pressable) {
        super(x, y, 11, 7, new StringTextComponent(""), pressable);
        this.sortDir = sortDir;
        this.visibleRegionStart = visibleRegionStart;
        this.visibleRegionHeight = visibleRegionHeight;
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partial) {
        if (mouseY >= visibleRegionStart && mouseY < visibleRegionStart + visibleRegionHeight) {
            Minecraft.getInstance().getTextureManager().bindTexture(SERVER_SELECTION_BUTTONS);
            this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int renderY = y - (sortDir == 1 ? 20 : 5);
            RenderSystem.enableBlend();
            if (active && isHovered) {
                RenderSystem.color4f(1f, 1f, 1f, 1f);
            } else if (active) {
                RenderSystem.color4f(1f, 1f, 1f, 0.75f);
            } else {
                RenderSystem.color4f(1f, 1f, 1f, 0.25f);
            }

            if (isHovered && active) {
                blit(matrixStack, x - 5, renderY, sortDir == 1 ? 64 : 96, 32, 32, 32);
            } else {
                blit(matrixStack, x - 5, renderY, sortDir == 1 ? 64 : 96, 0, 32, 32);
            }

            RenderSystem.disableBlend();
            RenderSystem.color4f(1f, 1f, 1f, 1f);
        }
    }

}
