package net.blay09.mods.waystones.client.gui;

import cpw.mods.fml.client.FMLClientHandler;
import net.blay09.mods.waystones.PlayerWaystoneData;
import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.Waystones;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

public class GuiButtonWarp extends GuiButton {

	private final GuiContainer parentScreen;

	public GuiButtonWarp(GuiContainer parentScreen) {
		super(-1, 0, 0, 16, 16, null);
		this.parentScreen = parentScreen;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if(visible) {
			xPosition = parentScreen.guiLeft + WaystoneConfig.teleportButtonX;
			yPosition = parentScreen.guiTop + WaystoneConfig.teleportButtonY;
			field_146123_n = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
			mc.getTextureManager().bindTexture(TextureMap.locationItemsTexture);
			EntityPlayer entityPlayer = FMLClientHandler.instance().getClientPlayerEntity();
			if(!PlayerWaystoneData.canFreeWarp(entityPlayer) || PlayerWaystoneData.getLastWaystone(entityPlayer) == null) {
				GL11.glColor4f(0.5f, 0.5f, 0.5f, 0.5f);
			} else if(field_146123_n) {
				GL11.glColor4f(1f, 1f, 1f, 1f);
			} else {
				GL11.glColor4f(0.8f, 0.8f, 0.8f, 0.8f);
			}
			drawTexturedModelRectFromIcon(xPosition, yPosition, Waystones.itemReturnScroll.getIconFromDamage(0), width, height);
		}
	}

	public boolean isHovered() {
		return field_146123_n;
	}
}
