package net.blay09.mods.waystones.client.gui;

import net.blay09.mods.waystones.PlayerWaystoneData;
import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.Waystones;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.FMLClientHandler;

public class GuiButtonInventoryWarp extends GuiButton {

	private final GuiContainer parentScreen;
	private final ItemStack iconItem;

	public GuiButtonInventoryWarp(GuiContainer parentScreen) {
		super(-1, 0, 0, 16, 16, "");
		this.parentScreen = parentScreen;
		this.iconItem = new ItemStack(Waystones.itemReturnScroll);
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if(visible) {
			xPosition = parentScreen.guiLeft + WaystoneConfig.teleportButtonX;
			yPosition = parentScreen.guiTop + WaystoneConfig.teleportButtonY;
			hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
			mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			EntityPlayer entityPlayer = FMLClientHandler.instance().getClientPlayerEntity();
			if(!PlayerWaystoneData.canFreeWarp(entityPlayer) || PlayerWaystoneData.getLastWaystone(entityPlayer) == null) {
				GlStateManager.color(0.5f, 0.5f, 0.5f, 0.5f);
			} else if(hovered) {
				GlStateManager.color(1f, 1f, 1f, 1f);
			} else {
				GlStateManager.color(0.8f, 0.8f, 0.8f, 0.8f);
			}
			Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(iconItem, xPosition, yPosition);
		}
	}

	public boolean isHovered() {
		return hovered;
	}
}
