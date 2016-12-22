package net.blay09.mods.waystones.client.gui;

import net.blay09.mods.waystones.PlayerWaystoneHelper;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.MessageWarpReturn;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.FMLClientHandler;

public class GuiConfirmInventoryButtonReturn extends GuiYesNo implements GuiYesNoCallback {
	private final String waystoneName;

	public GuiConfirmInventoryButtonReturn() {
		super(new GuiYesNoCallback() {
			@Override
			public void confirmClicked(boolean result, int id) {
				if(result) {
					NetworkHandler.channel.sendToServer(new MessageWarpReturn());
				}
				Minecraft.getMinecraft().displayGuiScreen(null);
			}
		}, I18n.format("gui.waystones:confirmReturn"), "", 0);
		this.waystoneName = getWaystoneName();
	}

	private static String getWaystoneName() {
		WaystoneEntry lastEntry = PlayerWaystoneHelper.getLastWaystone(FMLClientHandler.instance().getClientPlayerEntity());
		if(lastEntry != null) {
			return TextFormatting.GRAY + I18n.format("gui.waystones:confirmReturn.boundTo", lastEntry.getName());
		}
		return TextFormatting.GRAY + I18n.format("gui.waystones:confirmReturn.noWaystoneActive");
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		drawCenteredString(fontRendererObj, waystoneName, width / 2, 100, 0xFFFFFF);
	}
}
