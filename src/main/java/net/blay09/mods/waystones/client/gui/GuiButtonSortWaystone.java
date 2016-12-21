package net.blay09.mods.waystones.client.gui;

import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class GuiButtonSortWaystone extends GuiButtonExt {

	private static final ResourceLocation SERVER_SELECTION_BUTTONS = new ResourceLocation("textures/gui/server_selection.png");
	private final GuiButtonWaystoneEntry parentButton;
	private final int sortDir;

	public GuiButtonSortWaystone(int id, int x, int y, GuiButtonWaystoneEntry parentButton, int sortDir) {
		super(id, x, y, "");
		this.width = 11;
		this.height = 7;
		this.parentButton = parentButton;
		this.sortDir = sortDir;
	}

	public WaystoneEntry getWaystone() {
		return parentButton.getWaystone();
	}

	public int getSortDir() {
		return sortDir;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		GlStateManager.color(1f, 1f, 1f, 1f);
		mc.getTextureManager().bindTexture(SERVER_SELECTION_BUTTONS);
		if (this.visible && mouseY >= parentButton.yPosition && mouseY < parentButton.yPosition + parentButton.height) {
			this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
			if (hovered) {
				Gui.drawModalRectWithCustomSizedTexture(xPosition - 5, yPosition - 5 - (sortDir == 1 ? 15 : 0), 96f - (sortDir == 1 ? 32f : 0f), 32f, 32, 32, 256f, 256f);
			} else {
				Gui.drawModalRectWithCustomSizedTexture(xPosition - 5, yPosition - 5 - (sortDir == 1 ? 15 : 0), 96f - (sortDir == 1 ? 32f : 0f), 0f, 32, 32, 256f, 256f);
			}
		}
	}
}
