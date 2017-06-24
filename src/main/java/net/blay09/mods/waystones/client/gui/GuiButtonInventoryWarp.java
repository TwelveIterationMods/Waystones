package net.blay09.mods.waystones.client.gui;

import net.blay09.mods.waystones.PlayerWaystoneHelper;
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
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if(visible) {
			x = parentScreen.guiLeft + WaystoneConfig.client.teleportButtonX;
			y = parentScreen.guiTop + WaystoneConfig.client.teleportButtonY;
			hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
			mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			EntityPlayer entityPlayer = FMLClientHandler.instance().getClientPlayerEntity();
			if(!PlayerWaystoneHelper.canFreeWarp(entityPlayer) || PlayerWaystoneHelper.getLastWaystone(entityPlayer) == null) {
				GlStateManager.color(0.5f, 0.5f, 0.5f, 0.5f);
			} else if(hovered) {
				GlStateManager.color(1f, 1f, 1f, 1f);
			} else {
				GlStateManager.color(0.8f, 0.8f, 0.8f, 0.8f);
			}
			Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(iconItem, x, y);
		}
	}

	public boolean isHovered() {
		return hovered;
	}
}
