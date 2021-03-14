package net.blay09.mods.waystones.client.gui.widget;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.blay09.mods.waystones.api.IWaystone;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;

import java.util.Collections;
import java.util.List;

public class RemoveWaystoneButton extends Button implements ITooltipProvider {

    private static final ResourceLocation BEACON = new ResourceLocation("textures/gui/container/beacon.png");

    private final List<ITextComponent> tooltip;
    private final List<ITextComponent> activeTooltip;
    private final int visibleRegionStart;
    private final int visibleRegionHeight;
    private static boolean shiftGuard;

    public RemoveWaystoneButton(int x, int y, int visibleRegionStart, int visibleRegionHeight, IWaystone waystone, IPressable pressable) {
        super(x, y, 13, 13, new StringTextComponent(""), pressable);
        this.visibleRegionStart = visibleRegionStart;
        this.visibleRegionHeight = visibleRegionHeight;
        tooltip = Lists.newArrayList(new TranslationTextComponent("gui.waystones.waystone_selection.hold_shift_to_delete"));
        activeTooltip = Lists.newArrayList(new TranslationTextComponent("gui.waystones.waystone_selection.click_to_delete"));
        if (waystone.isGlobal()) {
            TranslationTextComponent component = new TranslationTextComponent("gui.waystones.waystone_selection.deleting_global_for_all");
            component.mergeStyle(TextFormatting.DARK_RED);
            tooltip.add(component);
            activeTooltip.add(component);
        }
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
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partial) {
        boolean shiftDown = Screen.hasShiftDown();
        if (!shiftDown) {
            shiftGuard = false;
        }
        active = !shiftGuard && shiftDown;

        if (mouseY >= visibleRegionStart && mouseY < visibleRegionStart + visibleRegionHeight) {
            RenderSystem.color4f(1f, 1f, 1f, 1f);
            Minecraft.getInstance().getTextureManager().bindTexture(BEACON);
            if (isHovered && active) {
                RenderSystem.color4f(1f, 1f, 1f, 1f);
            } else {
                RenderSystem.color4f(0.5f, 0.5f, 0.5f, 0.5f);
            }
            blit(matrixStack, x, y, 114, 223, 13, 13);
            RenderSystem.color4f(1f, 1f, 1f, 1f);
        }
    }

    @Override
    public boolean shouldShowTooltip() {
        return isHovered;
    }

    @Override
    public List<ITextComponent> getTooltip() {
        return active ? activeTooltip : tooltip;
    }
}
