package net.blay09.mods.waystones.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.WaystoneBlock;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.tileentity.WaystoneTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.ResourceLocation;

public class WaystoneRenderer extends TileEntityRenderer<WaystoneTileEntity> {

    private static final ResourceLocation textureActive = new ResourceLocation(Waystones.MOD_ID, "textures/entity/waystone_active.png");

    private final ModelWaystone model = new ModelWaystone();

    @Override
    public void render(WaystoneTileEntity tileEntity, double x, double y, double z, float partialTicks, int destroyStage) {
        BlockState state = tileEntity.getBlockState();
        if (state.get(WaystoneBlock.HALF) != DoubleBlockHalf.LOWER) {
            return;
        }

        float angle = state.get(WaystoneBlock.FACING).getHorizontalAngle();
        GlStateManager.pushMatrix();
        GlStateManager.translated(x + 0.5, y, z + 0.5);
        GlStateManager.rotatef(angle, 0f, 1f, 0f);
        GlStateManager.rotatef(-180f, 1f, 0f, 0f);
        GlStateManager.scalef(0.5f, 0.5f, 0.5f);
        boolean isActivated = PlayerWaystoneManager.isWaystoneActivated(Minecraft.getInstance().player, tileEntity.getWaystone());
        if (isActivated) {
            bindTexture(textureActive);
            GlStateManager.scalef(1.05f, 1.05f, 1.05f);
            if (!WaystoneConfig.CLIENT.disableTextGlow.get()) {
                Minecraft.getInstance().gameRenderer.disableLightmap();
            }
            model.renderPillar();
            if (!WaystoneConfig.CLIENT.disableTextGlow.get()) {
                Minecraft.getInstance().gameRenderer.enableLightmap();
            }
        }
        GlStateManager.popMatrix();
    }
}
