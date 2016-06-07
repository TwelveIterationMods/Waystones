package net.blay09.mods.waystones.client.gui;

import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.EnumChatFormatting;

public class GuiButtonWaystone extends GuiButton {

	private final WaystoneEntry waystone;

	public GuiButtonWaystone(int id, int x, int y, WaystoneEntry waystone) {
		super(id, x, y, (waystone.isGlobal() ? EnumChatFormatting.YELLOW : "") +  waystone.getName());
		this.waystone = waystone;
	}

	public WaystoneEntry getWaystone() {
		return waystone;
	}

}
