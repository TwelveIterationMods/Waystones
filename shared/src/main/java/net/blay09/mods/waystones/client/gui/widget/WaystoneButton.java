package net.blay09.mods.waystones.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class WaystoneButton extends Button {

    private static final ResourceLocation ENCHANTMENT_TABLE_GUI_TEXTURE = new ResourceLocation("textures/gui/container/enchanting_table.png");

    private final int xpLevelCost;

    public WaystoneButton(int x, int y, IWaystone waystone, int xpLevelCost, OnPress pressable) {
        super(x, y, 200, 20, getWaystoneNameComponent(waystone), pressable, Button.DEFAULT_NARRATION);
        Player player = Minecraft.getInstance().player;
        this.xpLevelCost = xpLevelCost;
        if (player == null || !PlayerWaystoneManager.mayTeleportToWaystone(player, waystone)) {
            active = false;
        } else if (player.experienceLevel < xpLevelCost && !player.getAbilities().instabuild) {
            active = false;
        }
    }

    private static Component getWaystoneNameComponent(IWaystone waystone) {
        String effectiveName = waystone.getName();
        if (effectiveName.isEmpty()) {
            effectiveName = I18n.get("gui.waystones.waystone_selection.unnamed_waystone");
        }
        final var textComponent = Component.literal(effectiveName);
        if (waystone.isGlobal()) {
            textComponent.withStyle(ChatFormatting.YELLOW);
        }
        return textComponent;
    }

    @Override
    public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.renderButton(matrixStack, mouseX, mouseY, partialTicks);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        Minecraft mc = Minecraft.getInstance();
        if (xpLevelCost > 0) {
            boolean canAfford = Objects.requireNonNull(mc.player).experienceLevel >= xpLevelCost || mc.player.getAbilities().instabuild;
            RenderSystem.setShaderTexture(0, ENCHANTMENT_TABLE_GUI_TEXTURE);
            blit(matrixStack, getX() + 2, getY() + 2, (Math.min(xpLevelCost, 3) - 1) * 16, 223 + (!canAfford ? 16 : 0), 16, 16);

            if (xpLevelCost > 3) {
                mc.font.draw(matrixStack, "+", getX() + 17, getY() + 6, 0xC8FF8F);
            }

            if (isHovered && mouseX <= getX() + 16) {
                final List<Component> tooltip = new ArrayList<>();
                final var levelRequirementText = Component.translatable("gui.waystones.waystone_selection.level_requirement", xpLevelCost);
                levelRequirementText.withStyle(canAfford ? ChatFormatting.GREEN : ChatFormatting.RED);
                tooltip.add(levelRequirementText);
                final Screen screen = Minecraft.getInstance().screen;
                Objects.requireNonNull(screen).renderTooltip(matrixStack, tooltip, Optional.empty(), mouseX, mouseY + mc.font.lineHeight);
            }
        }
    }

}
