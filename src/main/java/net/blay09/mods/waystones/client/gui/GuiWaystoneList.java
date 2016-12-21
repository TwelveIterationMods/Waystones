package net.blay09.mods.waystones.client.gui;

import net.blay09.mods.waystones.WarpMode;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.MessageSortWaystone;
import net.blay09.mods.waystones.network.message.MessageWarpStone;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;
import java.util.Iterator;

public class GuiWaystoneList extends GuiScreen {

	private final WaystoneEntry[] entries;
	private final WarpMode warpMode;
	private final EnumHand hand;
	private GuiButton btnPrevPage;
	private GuiButton btnNextPage;
	private int pageOffset;

	public GuiWaystoneList(WaystoneEntry[] entries, WarpMode warpMode, EnumHand hand) {
		this.entries = entries;
		this.warpMode = warpMode;
		this.hand = hand;
	}

	@Override
	public void initGui() {
		btnPrevPage = new GuiButton(0, width / 2 - 100, height / 2 + 40, 95, 20, I18n.format("gui.waystones:warpStone.previousPage"));
		buttonList.add(btnPrevPage);

		btnNextPage = new GuiButton(1, width / 2 + 5, height / 2 + 40, 95, 20, I18n.format("gui.waystones:warpStone.nextPage"));
		buttonList.add(btnNextPage);

		updateList();
	}

	public void updateList() {
		final int buttonsPerPage = 4;

		btnPrevPage.enabled = pageOffset > 0;
		btnNextPage.enabled = pageOffset < (entries.length - 1) / buttonsPerPage;

		Iterator<GuiButton> it = buttonList.iterator();
		while(it.hasNext()) {
			GuiButton button = it.next();
			if (button instanceof GuiButtonWaystoneEntry || button instanceof GuiButtonSortWaystone) {
				it.remove();
			}
		}

		int id = 2;
		int y = 0;
		for(int i = 0; i < buttonsPerPage; i++) {
			int entryIndex = pageOffset * buttonsPerPage + i;
			if(entryIndex >= 0 && entryIndex < entries.length) {
				GuiButtonWaystoneEntry btnWaystone = new GuiButtonWaystoneEntry(id, width / 2 - 100, height / 2 - 60 + y, entries[entryIndex]);
				if(entries[entryIndex].getDimensionId() != Minecraft.getMinecraft().world.provider.getDimension()) {
					if(!Waystones.getConfig().interDimension && !(!entries[entryIndex].isGlobal() || !Waystones.getConfig().globalInterDimension)) {
						btnWaystone.enabled = false;
					}
				}
				buttonList.add(btnWaystone);
				id++;

				GuiButtonSortWaystone sortUp = new GuiButtonSortWaystone(id, width / 2 + 108, height / 2 - 60 + y + 2, btnWaystone, -1);
				if(entryIndex == 0) {
					sortUp.visible = false;
				}
				buttonList.add(sortUp);
				id++;

				GuiButtonSortWaystone sortDown = new GuiButtonSortWaystone(id, width / 2 + 108, height / 2 - 60 + y + 11, btnWaystone, 1);
				if(entryIndex == entries.length - 1) {
					sortDown.visible = false;
				}
				buttonList.add(sortDown);
				id++;

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
		} else if(button instanceof GuiButtonWaystoneEntry) {
			NetworkHandler.channel.sendToServer(new MessageWarpStone(((GuiButtonWaystoneEntry) button).getWaystone(), warpMode, hand));
			mc.displayGuiScreen(null);
		} else if(button instanceof GuiButtonSortWaystone) {
			WaystoneEntry waystoneEntry = ((GuiButtonSortWaystone) button).getWaystone();
			int index = ArrayUtils.indexOf(entries, waystoneEntry);
			int sortDir = ((GuiButtonSortWaystone) button).getSortDir();
			int otherIndex = index + sortDir;
			if(index == -1 || otherIndex < 0 || otherIndex >= entries.length) {
				return;
			}
			WaystoneEntry swap = entries[index];
			entries[index] = entries[otherIndex];
			entries[otherIndex] = swap;
			NetworkHandler.channel.sendToServer(new MessageSortWaystone(index, otherIndex));
			updateList();
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawWorldBackground(0);
		super.drawScreen(mouseX, mouseY, partialTicks);
		GL11.glColor4f(1f, 1f, 1f, 1f);
		drawRect(width / 2 - 50, height / 2 - 50, width / 2 + 50, height / 2 + 50, 0xFFFFFF);
		drawCenteredString(fontRendererObj, I18n.format("gui.waystones:warpStone.selectDestination"), width / 2, height / 2 - 85, 0xFFFFFF);
		if(entries.length == 0) {
			drawCenteredString(fontRendererObj, TextFormatting.RED + I18n.format("waystones:scrollNotBound"), width / 2, height / 2 - 20, 0xFFFFFF);
		}
	}

}
