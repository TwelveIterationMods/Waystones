package net.blay09.mods.waystones.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

import java.util.Objects;
import java.util.function.Supplier;

public class WaystoneInventoryButton extends Button {

    private static final ResourceLocation INVENTORY_BUTTON_TEXTURE = new ResourceLocation(Waystones.MOD_ID, "textures/gui/inventory_button.png");

    private final ContainerScreen<?> parentScreen;
    private final ItemStack iconItem;
    private final ItemStack iconItemHovered;
    private final Supplier<Boolean> visiblePredicate;
    private final Supplier<Integer> xPosition;
    private final Supplier<Integer> yPosition;

    public WaystoneInventoryButton(ContainerScreen<?> parentScreen, IPressable pressable, Supplier<Boolean> visiblePredicate, Supplier<Integer> xPosition, Supplier<Integer> yPosition) {
        super(0, 0, 16, 16, new StringTextComponent(""), pressable);
        this.parentScreen = parentScreen;
        this.visiblePredicate = visiblePredicate;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.iconItem = new ItemStack(ModItems.boundScroll);
        this.iconItemHovered = new ItemStack(ModItems.warpScroll);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        visible = visiblePredicate.get();
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            x = parentScreen.getGuiLeft() + xPosition.get();
            y = parentScreen.getGuiTop() + yPosition.get();
            isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;

            PlayerEntity player = Minecraft.getInstance().player;
            if (PlayerWaystoneManager.canUseInventoryButton(Objects.requireNonNull(player))) {
                ItemStack icon = isHovered ? iconItemHovered : iconItem;
                ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
                itemRenderer.renderItemAndEffectIntoGUI(icon, x, y);
            } else {
                Minecraft.getInstance().getTextureManager().bindTexture(INVENTORY_BUTTON_TEXTURE);
                RenderSystem.enableBlend();
                RenderSystem.color4f(1f, 1f, 1f, 0.5f);
                blit(matrixStack, x, y, 0, 0, 16, 16, 16, 16);
                RenderSystem.color4f(1f, 1f, 1f, 1f);
                RenderSystem.disableBlend();
            }
        }
    }
}
