package net.blay09.mods.waystones.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.blay09.mods.waystones.core.IWaystone;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;

public class RemoveWaystoneButton extends Button {

    private static final ResourceLocation BEACON = new ResourceLocation("textures/gui/container/beacon.png");
    private final WaystoneEntryButton parentButton;

    private boolean shiftGuard;

    public RemoveWaystoneButton(int x, int y, WaystoneEntryButton parentButton, IPressable pressable) {
        super(x, y, 13, 13, "", pressable);
        this.parentButton = parentButton;
    }

    public IWaystone getWaystone() {
        return parentButton.getWaystone();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            shiftGuard = true;
            return true;
        }

        return false;
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partial) {
        this.visible = shiftGuard != Screen.hasShiftDown();
        if (this.visible) {
            shiftGuard = false;
        }
        if (this.visible && mouseY >= parentButton.y && mouseY < parentButton.y + parentButton.getHeight()) {
            super.renderButton(mouseX, mouseY, partial);
            GlStateManager.color4f(1f, 1f, 1f, 1f);
            Minecraft.getInstance().getTextureManager().bindTexture(BEACON);
            blit(x + 3, y + 3, 114f, 223f, 13, 13, 7, 7);
        }
    }

}
