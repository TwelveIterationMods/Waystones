package net.blay09.mods.waystones.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.blay09.mods.waystones.api.IWaystone;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class SortWaystoneButton extends GuiButtonExt {

    private static final ResourceLocation SERVER_SELECTION_BUTTONS = new ResourceLocation("textures/gui/server_selection.png");
    private final IWaystone waystone;
    private final int sortDir;

    public SortWaystoneButton(int x, int y, IWaystone waystone, int sortDir, IPressable pressable) {
        super(x, y, 11, 7, "", pressable);
        this.waystone = waystone;
        this.sortDir = sortDir;
    }

    public IWaystone getWaystone() {
        return waystone;
    }

    public int getSortDir() {
        return sortDir;
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partial) {
        if (this.visible) {// TODO && mouseY >= parentButton.y && mouseY < parentButton.y + parentButton.getHeight()) {
            GlStateManager.color4f(1f, 1f, 1f, 1f);
            Minecraft.getInstance().getTextureManager().bindTexture(SERVER_SELECTION_BUTTONS);
            this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            if (isHovered) {
                blit(x - 5, y - 5 - (sortDir == 1 ? 15 : 0), 96 - (sortDir == 1 ? 32 : 0), 32, 32, 32);
            } else {
                blit(x - 5, y - 5 - (sortDir == 1 ? 15 : 0), 96 - (sortDir == 1 ? 32 : 0), 0, 32, 32);
            }
        }
    }

}
