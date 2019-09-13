package net.blay09.mods.waystones.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.blay09.mods.waystones.PlayerWaystoneHelper;
import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class InventoryWarpButton extends Button {

    private final ContainerScreen<?> parentScreen;
    private final ItemStack iconItem;

    public InventoryWarpButton(ContainerScreen<?> parentScreen, IPressable pressable) {
        super(0, 0, 16, 16, "", pressable);
        this.parentScreen = parentScreen;
        this.iconItem = new ItemStack(ModItems.returnScroll);
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            x = parentScreen.getGuiLeft() + WaystoneConfig.CLIENT.teleportButtonX.get();
            y = parentScreen.getGuiTop() + WaystoneConfig.CLIENT.teleportButtonY.get();
            isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
            Minecraft.getInstance().getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            PlayerEntity entityPlayer = Minecraft.getInstance().player;
            if (!PlayerWaystoneHelper.canFreeWarp(entityPlayer) || PlayerWaystoneHelper.getLastWaystone(entityPlayer) == null) {
                GlStateManager.color4f(0.5f, 0.5f, 0.5f, 0.5f);
            } else if (isHovered) {
                GlStateManager.color4f(1f, 1f, 1f, 1f);
            } else {
                GlStateManager.color4f(0.8f, 0.8f, 0.8f, 0.8f);
            }
            Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGUI(iconItem, x, y);
        }
    }
}
