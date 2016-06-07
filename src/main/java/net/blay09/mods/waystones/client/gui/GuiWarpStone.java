package net.blay09.mods.waystones.client.gui;

import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.MessageWarpStone;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import org.lwjgl.opengl.GL11;

import java.util.Iterator;

public class GuiWarpStone extends GuiScreen {

	private final WaystoneEntry[] entries;
	private GuiButton btnPrevPage;
	private GuiButton btnNextPage;
	private int pageOffset;
	private boolean isFree;

	public GuiWarpStone(WaystoneEntry[] entries, boolean isFree) {
		this.entries = entries;
		this.isFree = isFree;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void initGui() {
		btnPrevPage = new GuiButton(0, width / 2 - 100, height / 2 + 40, 95, 20, I18n.format("gui.waystones:warpStone.previousPage"));
		buttonList.add(btnPrevPage);

		btnNextPage = new GuiButton(1, width / 2 + 5, height / 2 + 40, 95, 20, I18n.format("gui.waystones:warpStone.nextPage"));
		buttonList.add(btnNextPage);

		updateList();
	}

	@SuppressWarnings("unchecked")
	public void updateList() {
		final int buttonsPerPage = 4;

		btnPrevPage.enabled = pageOffset > 0;
		btnNextPage.enabled = pageOffset < (entries.length - 1) / buttonsPerPage;

		Iterator it = buttonList.iterator();
		while(it.hasNext()) {
			if (it.next() instanceof GuiButtonWaystone) {
				it.remove();
			}
		}

		int y = 0;
		for(int i = 0; i < buttonsPerPage; i++) {
			int entryIndex = pageOffset * buttonsPerPage + i;
			if(entryIndex >= 0 && entryIndex < entries.length) {
				GuiButtonWaystone btnWaystone = new GuiButtonWaystone(2 + i, width / 2 - 100, height / 2 - 60 + y, entries[entryIndex]);
				if(entries[entryIndex].getDimensionId() != Minecraft.getMinecraft().theWorld.provider.dimensionId) {
					if(!Waystones.getConfig().interDimension && !(!entries[entryIndex].isGlobal() || !Waystones.getConfig().globalInterDimension)) {
						btnWaystone.enabled = false;
					}
				}
				buttonList.add(btnWaystone);
				y += 22;
			}
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if(button == btnNextPage) {
			pageOffset++;
			updateList();
		} else if(button == btnPrevPage) {
			pageOffset--;
			updateList();
		} else if(button instanceof GuiButtonWaystone) {
			NetworkHandler.channel.sendToServer(new MessageWarpStone(((GuiButtonWaystone) button).getWaystone(), isFree));
			mc.displayGuiScreen(null);
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawWorldBackground(0);
		super.drawScreen(mouseX, mouseY, partialTicks);
		GL11.glColor4f(1f, 1f, 1f, 1f);
		drawRect(width / 2 - 50, height / 2 - 50, width / 2 + 50, height / 2 + 50, 0xFFFFFF);
		drawCenteredString(fontRendererObj, I18n.format("gui.waystones:warpStone.selectDestination"), width / 2, height / 2 - 85, 0xFFFFFF);
	}

}
