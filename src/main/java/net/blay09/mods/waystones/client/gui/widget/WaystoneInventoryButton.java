package net.blay09.mods.waystones.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.config.WaystoneConfig;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class WaystoneInventoryButton extends Button {

    private static final ResourceLocation INVENTORY_BUTTON_TEXTURE = new ResourceLocation(Waystones.MOD_ID, "textures/gui/inventory_button.png");

    private final ContainerScreen<?> parentScreen;
    private final ItemStack iconItem;
    private final ItemStack iconItemHovered;

    public WaystoneInventoryButton(ContainerScreen<?> parentScreen, IPressable pressable) {
        super(0, 0, 16, 16, "", pressable);
        this.parentScreen = parentScreen;
        this.iconItem = new ItemStack(ModItems.boundScroll);
        this.iconItemHovered = new ItemStack(ModItems.warpScroll);
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            x = parentScreen.getGuiLeft() + WaystoneConfig.CLIENT.teleportButtonX.get();
            y = parentScreen.getGuiTop() + WaystoneConfig.CLIENT.teleportButtonY.get();
            isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;

            PlayerEntity player = Minecraft.getInstance().player;
            if (PlayerWaystoneManager.canUseInventoryButton(player)) {
                ItemStack icon = isHovered ? iconItemHovered : iconItem;
                ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
                itemRenderer.renderItemAndEffectIntoGUI(icon, x, y);
            } else {
                Minecraft.getInstance().getTextureManager().bindTexture(INVENTORY_BUTTON_TEXTURE);
                GlStateManager.enableBlend();
                GlStateManager.color4f(1f, 1f, 1f, 0.5f);
                blit(x, y, 0, 0, 16, 16, 16, 16);
                GlStateManager.color4f(1f, 1f, 1f, 1f);
                GlStateManager.disableBlend();
            }
        }
    }
}
