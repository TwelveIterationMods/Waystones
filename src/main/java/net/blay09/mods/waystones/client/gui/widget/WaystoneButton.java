package net.blay09.mods.waystones.client.gui.widget;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WarpMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WaystoneButton extends Button {

    private static final ResourceLocation ENCHANTMENT_TABLE_GUI_TEXTURE = new ResourceLocation("textures/gui/container/enchanting_table.png");

    private final int xpLevelCost;

    public WaystoneButton(int x, int y, IWaystone waystone, WarpMode warpMode, IPressable pressable) {
        super(x, y, 200, 20, getWaystoneNameComponent(waystone), pressable);
        PlayerEntity player = Minecraft.getInstance().player;
        this.xpLevelCost = Math.round(PlayerWaystoneManager.getExperienceLevelCost(Objects.requireNonNull(player), waystone, warpMode));
        if (!PlayerWaystoneManager.mayTeleportToWaystone(player, waystone)) {
            active = false;
        } else if (player.experienceLevel < xpLevelCost && !player.abilities.isCreativeMode) {
            active = false;
        }
    }

    private static ITextComponent getWaystoneNameComponent(IWaystone waystone) {
        final StringTextComponent textComponent = new StringTextComponent(waystone.getName());
        if (waystone.isGlobal()) {
            textComponent.getStyle().setFormatting(TextFormatting.YELLOW);
        }
        return textComponent;
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.renderButton(matrixStack, mouseX, mouseY, partialTicks);
        RenderSystem.color4f(1f, 1f, 1f, 1f);

        Minecraft mc = Minecraft.getInstance();
        if (xpLevelCost > 0) {
            boolean canAfford = Objects.requireNonNull(mc.player).experienceLevel >= xpLevelCost || mc.player.abilities.isCreativeMode;
            mc.getTextureManager().bindTexture(ENCHANTMENT_TABLE_GUI_TEXTURE);
            blit(matrixStack, x + 2, y + 2, (Math.min(xpLevelCost, 3) - 1) * 16, 223 + (!canAfford ? 16 : 0), 16, 16);

            if (xpLevelCost > 3) {
                mc.fontRenderer.drawString(matrixStack, "+", x + 17, y + 6, 0xC8FF8F);
            }

            if (isHovered && mouseX <= x + 16) {
                final List<ITextProperties> tooltip = new ArrayList<>();
                final TranslationTextComponent levelRequirementText = new TranslationTextComponent("gui.waystones.waystone_selection.level_requirement", xpLevelCost);
                levelRequirementText.getStyle().setFormatting(canAfford ? TextFormatting.GREEN : TextFormatting.RED);
                tooltip.add(levelRequirementText);
                GuiUtils.drawHoveringText(matrixStack, tooltip, mouseX, mouseY + mc.fontRenderer.FONT_HEIGHT, mc.getMainWindow().getWidth(), mc.getMainWindow().getHeight(), 200, mc.fontRenderer);
            }
            RenderSystem.disableLighting();
        }
    }

}
