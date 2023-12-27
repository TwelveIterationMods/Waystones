package net.blay09.mods.waystones.client.requirement;

import net.blay09.mods.waystones.requirement.CooldownRequirement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;

public class CooldownRequirementRenderer implements RequirementRenderer<CooldownRequirement> {
    @Override
    public void renderWidget(Player player, CooldownRequirement requirement, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks, int x, int y) {
        final var millisLeft = requirement.getCooldownMillisLeft(player);
        if (millisLeft <= 0) {
            return;
        }

        var secondsLeft = millisLeft / 1000;
        var minutesLeft = secondsLeft / 60;
        secondsLeft %= 60;
        final var timeLeft = String.format("%02d:%02d", minutesLeft, secondsLeft);
        final var font = Minecraft.getInstance().font;
        guiGraphics.drawString(font, timeLeft, x, y + font.lineHeight / 2, 0xFFFFAAAA);
    }
}