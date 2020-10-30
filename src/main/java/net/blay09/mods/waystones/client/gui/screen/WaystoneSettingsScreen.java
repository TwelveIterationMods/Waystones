package net.blay09.mods.waystones.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.container.WaystoneSettingsContainer;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.EditWaystoneMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ToggleWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

public class WaystoneSettingsScreen extends ContainerScreen<WaystoneSettingsContainer> {

    private TextFieldWidget textField;
    private Button btnDone;
    private ToggleWidget chkGlobal;
    private final TranslationTextComponent isGlobalText = new TranslationTextComponent("gui.waystones.waystone_settings.is_global");

    public WaystoneSettingsScreen(WaystoneSettingsContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        xSize = 270;
        ySize = 200;
    }

    @Override
    public void init() {
        // Leave no space for JEI!
        xSize = width;

        super.init();
        IWaystone waystone = container.getWaystone();
        String oldText = waystone.getName();
        if (textField != null) {
            oldText = textField.getText();
        }

        textField = new TextFieldWidget(Minecraft.getInstance().fontRenderer, width / 2 - 100, height / 2 - 20, 200, 20, textField, new StringTextComponent(""));
        textField.setMaxStringLength(128);
        textField.setText(oldText);
        textField.changeFocus(true);
        addButton(textField);
        setFocusedDefault(textField);

        btnDone = new Button(width / 2, height / 2 + 10, 100, 20, new TranslationTextComponent("gui.done"), button -> {
            if (textField.getText().isEmpty()) {
                textField.changeFocus(true);
                setListener(textField);
                return;
            }

            NetworkHandler.channel.sendToServer(new EditWaystoneMessage(waystone, textField.getText(), chkGlobal.isStateTriggered()));
        });
        addButton(btnDone);

        chkGlobal = new ToggleWidget(width / 2 - 100, height / 2 + 10, 20, 20, waystone.isGlobal());
        chkGlobal.initTextureValues(0, 0, 20, 20, new ResourceLocation(Waystones.MOD_ID, "textures/gui/checkbox.png"));
        if (!PlayerWaystoneManager.mayEditGlobalWaystones(Objects.requireNonNull(Minecraft.getInstance().player))) {
            chkGlobal.visible = false;
        }

        addButton(chkGlobal);

        getMinecraft().keyboardListener.enableRepeatEvents(true);
    }

    @Override
    public void onClose() {
        super.onClose();
        getMinecraft().keyboardListener.enableRepeatEvents(false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (chkGlobal.mouseClicked(mouseX, mouseY, button)) {
            chkGlobal.setStateTriggered(!chkGlobal.isStateTriggered());
            return true;
        }

        final int chkGlobalLabelX = width / 2 - 100 + 25;
        final int chkGlobalLabelY = height / 2 + 16;
        final int chkGlobalLabelWidth = getMinecraft().fontRenderer.getStringWidth(I18n.format("gui.waystones.waystone_settings.is_global"));
        if (mouseX >= chkGlobalLabelX && mouseX < chkGlobalLabelX + chkGlobalLabelWidth && mouseY >= chkGlobalLabelY && mouseY < chkGlobalLabelY + getMinecraft().fontRenderer.FONT_HEIGHT) {
            chkGlobal.setStateTriggered(!chkGlobal.isStateTriggered());
            return true;
        }

        if (textField.mouseClicked(mouseX, mouseY, button)) {
            return true;
        } else if(textField.isMouseOver(mouseX, mouseY) && button == 1) {
            textField.setText("");
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

        if (textField.keyPressed(keyCode, scanCode, modifiers) || textField.isFocused()) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                Objects.requireNonNull(getMinecraft().player).closeScreen();
            }

            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void tick() {
        textField.tick();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        drawString(matrixStack, font, getTitle(), width / 2 - 100, height / 2 - 35, 0xFFFFFF);

        if (chkGlobal.visible) {
            drawString(matrixStack, font, isGlobalText, width / 2 - 100 + 25, height / 2 + 16, 0xFFFFFF);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack p_230451_1_, int p_230451_2_, int p_230451_3_) {
    }
}
