package net.blay09.mods.waystones.client.requirement;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;

public interface RequirementRenderer<T> {
    void renderWidget(Player player, T requirement, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks, int x, int y);

    default int getWidth(T requirement) {
        return 16;
    }

    default int getOrder() {
        return 100;
    }
}
