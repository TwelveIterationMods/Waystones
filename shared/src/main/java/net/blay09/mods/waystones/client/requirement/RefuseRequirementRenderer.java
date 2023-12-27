package net.blay09.mods.waystones.client.requirement;

import net.blay09.mods.waystones.requirement.RefuseRequirement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class RefuseRequirementRenderer implements RequirementRenderer<RefuseRequirement> {

    private static final ResourceLocation CANCEL_SPRITE = new ResourceLocation("container/beacon/cancel");

    @Override
    public void renderWidget(Player player, RefuseRequirement requirement, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks, int x, int y) {
        guiGraphics.blitSprite(CANCEL_SPRITE, x, y, 16, 16);
    }
}
