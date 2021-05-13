package net.blay09.mods.waystones.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.container.WarpPlateContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;


public class WarpPlateScreen extends ContainerScreen<WarpPlateContainer> {

    private static final ResourceLocation WARP_PLATE_GUI_TEXTURES = new ResourceLocation(Waystones.MOD_ID, "textures/gui/container/warp_plate.png");

    public WarpPlateScreen(WarpPlateContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        ySize = 196;
        playerInventoryTitleY = 92;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        minecraft.getTextureManager().bindTexture(WARP_PLATE_GUI_TEXTURES);
        blit(matrixStack, guiLeft, guiTop, 0, 0, xSize, ySize);

        blit(matrixStack, guiLeft + 86, guiTop + 34, 176, 4, 4, (int) (10 * container.getAttunementProgress()));
        blit(matrixStack, guiLeft + 107 - (int) (10 * container.getAttunementProgress()), guiTop + 51, 176, 0, (int) (10 * container.getAttunementProgress()), 4);
        blit(matrixStack, guiLeft + 86, guiTop + 72 - (int) (10 * container.getAttunementProgress()), 176, 4, 4, (int) (10 * container.getAttunementProgress()));
        blit(matrixStack, guiLeft + 69, guiTop + 51, 176, 0, (int) (10 * container.getAttunementProgress()), 4);
    }

}
