package net.blay09.mods.waystones.client.gui.widget;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WarpMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiUtils;

public class WaystoneButton extends Button {

    private static final ResourceLocation ENCHANTMENT_TABLE_GUI_TEXTURE = new ResourceLocation("textures/gui/container/enchanting_table.png");

    private final int xpLevelCost;

    public WaystoneButton(int x, int y, IWaystone waystone, WarpMode warpMode, IPressable pressable) {
        super(x, y, 200, 20, (waystone.isGlobal() ? TextFormatting.YELLOW : "") + waystone.getName(), pressable);
        PlayerEntity player = Minecraft.getInstance().player;
        this.xpLevelCost = PlayerWaystoneManager.getExperienceLevelCost(player, waystone, warpMode);
        if (!PlayerWaystoneManager.mayTeleportToWaystone(player, waystone)) {
            active = false;
        } else if (player.experienceLevel < xpLevelCost && !player.abilities.isCreativeMode) {
            active = false;
        }
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks) {
        super.renderButton(mouseX, mouseY, partialTicks);
        GlStateManager.color4f(1f, 1f, 1f, 1f);

        Minecraft mc = Minecraft.getInstance();
        if (xpLevelCost > 0) {
            boolean canAfford = mc.player.experienceLevel >= xpLevelCost || mc.player.abilities.isCreativeMode;
            mc.getTextureManager().bindTexture(ENCHANTMENT_TABLE_GUI_TEXTURE);
            blit(x + 2, y + 2, (Math.min(xpLevelCost, 3) - 1) * 16, 223 + (!canAfford ? 16 : 0), 16, 16);

            if (xpLevelCost > 3) {
                mc.fontRenderer.drawString("+", x + 17, y + 6, 0xC8FF8F);
            }

            if (isHovered && mouseX <= x + 16) {
                GuiUtils.drawHoveringText(Lists.newArrayList((canAfford ? TextFormatting.GREEN : TextFormatting.RED) + I18n.format("tooltip.waystones:levelRequirement", xpLevelCost)), mouseX, mouseY + mc.fontRenderer.FONT_HEIGHT, mc.mainWindow.getWidth(), mc.mainWindow.getHeight(), 200, mc.fontRenderer);
            }
            GlStateManager.disableLighting();
        }
    }

}
