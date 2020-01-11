package net.blay09.mods.waystones.client.gui.widget;

import net.blay09.mods.waystones.config.WaystoneConfig;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class InventoryWarpButton extends Button {

    private final ContainerScreen<?> parentScreen;
    private final ItemStack iconItem;
    private final ItemStack iconItemDisabled;
    private final ItemStack iconItemHovered;

    public InventoryWarpButton(ContainerScreen<?> parentScreen, IPressable pressable) {
        super(0, 0, 16, 16, "", pressable);
        this.parentScreen = parentScreen;
        this.iconItem = new ItemStack(ModItems.returnScroll);
        this.iconItemDisabled = new ItemStack(ModItems.returnScroll);
        this.iconItemHovered = new ItemStack(ModItems.returnScroll);
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            x = parentScreen.getGuiLeft() + WaystoneConfig.CLIENT.teleportButtonX.get();
            y = parentScreen.getGuiTop() + WaystoneConfig.CLIENT.teleportButtonY.get();
            isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;

            ItemStack icon = iconItem;
            PlayerEntity player = Minecraft.getInstance().player;
            if (!PlayerWaystoneManager.canUseInventoryButton(player)) {
                icon = iconItemDisabled;
            } else if (isHovered) {
                icon = iconItemHovered;
            }

            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            itemRenderer.renderItemAndEffectIntoGUI(icon, x, y);
        }
    }
}
