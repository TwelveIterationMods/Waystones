package net.blay09.mods.waystones.client.gui;

import cpw.mods.fml.client.config.GuiCheckBox;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.block.TileWaystone;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.MessageWaystoneName;
import net.blay09.mods.waystones.util.BlockPos;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

public class GuiWaystoneName extends GuiScreen {

	private final TileWaystone tileWaystone;
	private GuiTextField textField;
	private GuiButton btnDone;
	private GuiCheckBox chkGlobal;

	public GuiWaystoneName(TileWaystone tileWaystone) {
		this.tileWaystone = tileWaystone;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void initGui() {
		String oldText = tileWaystone.getWaystoneName();
		if(textField != null) {
			oldText = textField.getText();
		}
		textField = new GuiTextField(fontRendererObj, width / 2 - 100, height / 2 - 20, 200, 20);
		textField.setText(oldText);
		textField.setFocused(true);
		btnDone = new GuiButton(0, width / 2, height / 2 + 10, 100, 20, I18n.format("gui.done"));
		buttonList.add(btnDone);

		chkGlobal = new GuiCheckBox(1, width / 2 - 100, height / 2 + 15, " " + I18n.format("gui.waystones:editWaystone.isGlobal"), WaystoneManager.getServerWaystone(tileWaystone.getWaystoneName()) != null);
		if(!Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode) {
			chkGlobal.visible = false;
		}
		buttonList.add(chkGlobal);

		Keyboard.enableRepeatEvents(true);
	}

	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if(button == btnDone) {
			NetworkHandler.channel.sendToServer(new MessageWaystoneName(new BlockPos(tileWaystone), textField.getText(), chkGlobal.isChecked()));
			mc.displayGuiScreen(null);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		textField.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) {
		if(keyCode == Keyboard.KEY_RETURN) {
			actionPerformed(btnDone);
			return;
		}
		super.keyTyped(typedChar, keyCode);
		textField.textboxKeyTyped(typedChar, keyCode);
	}

	@Override
	public void updateScreen() {
		textField.updateCursorCounter();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawWorldBackground(0);
		super.drawScreen(mouseX, mouseY, partialTicks);

		fontRendererObj.drawString(I18n.format("gui.waystones:editWaystone.enterName"), width / 2 - 100, height / 2 - 35, 0xFFFFFF);
		textField.drawTextBox();
	}

}
