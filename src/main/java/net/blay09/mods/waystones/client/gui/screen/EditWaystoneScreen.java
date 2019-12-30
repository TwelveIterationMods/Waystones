package net.blay09.mods.waystones.client.gui.screen;

import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.core.IWaystone;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.MessageEditWaystone;
import net.blay09.mods.waystones.util.WaystoneActivatedEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import org.lwjgl.glfw.GLFW;

public class EditWaystoneScreen extends Screen {

    private final IWaystone tileWaystone;
    private TextFieldWidget textField;
    private Button btnDone;
    private GuiCheckBox chkGlobal;
    private boolean fromSelectionGui;

    public EditWaystoneScreen(IWaystone waystone, boolean fromSelectionGui) {
        super(new TranslationTextComponent("gui.waystones:editWaystone.enterName"));
        this.tileWaystone = waystone;
        this.fromSelectionGui = fromSelectionGui;
    }

    @Override
    public void init() {
        super.init();
        String oldText = tileWaystone.getName();
        if (textField != null) {
            oldText = textField.getText();
        }

        textField = new TextFieldWidget(Minecraft.getInstance().fontRenderer, width / 2 - 100, height / 2 - 20, 200, 20, textField, "");
        textField.setMaxStringLength(128);
        textField.setText(oldText);
        textField.setFocused2(true);
        addButton(textField);

        btnDone = new Button(width / 2, height / 2 + 10, 100, 20, I18n.format("gui.done"), button -> {
            if (textField.getText().isEmpty()) {
                textField.setFocused2(true);
                return;
            }

            NetworkHandler.channel.sendToServer(new MessageEditWaystone(tileWaystone.getPos(), textField.getText(), chkGlobal.isChecked(), fromSelectionGui));

            if (!fromSelectionGui) {
                Minecraft.getInstance().player.closeScreen();
            }

            if (tileWaystone.getName().isEmpty()) {
                MinecraftForge.EVENT_BUS.post(new WaystoneActivatedEvent(textField.getText(), tileWaystone.getPos(), tileWaystone.getDimensionType()));
            }
        });
        addButton(btnDone);

        chkGlobal = new GuiCheckBox(width / 2 - 100, height / 2 + 15, " " + I18n.format("gui.waystones:editWaystone.isGlobal"), tileWaystone.isGlobal());
        if (!WaystoneConfig.SERVER.allowEveryoneGlobal.get() && (!Minecraft.getInstance().player.abilities.isCreativeMode)) {
            chkGlobal.visible = false;
        }

        addButton(chkGlobal);

        getMinecraft().keyboardListener.enableRepeatEvents(true);
    }

    @Override
    public void removed() {
        super.removed();
        getMinecraft().keyboardListener.enableRepeatEvents(false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (textField.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER) {
            btnDone.onPress();
            return true;
        }

        if (textField.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void tick() {
        textField.tick();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        renderBackground();
        super.render(mouseX, mouseY, partialTicks);

        Minecraft.getInstance().fontRenderer.drawString(getTitle().getFormattedText(), width / 2f - 100, height / 2f - 35, 0xFFFFFF);
    }

}
