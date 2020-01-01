package net.blay09.mods.waystones.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.block.WaystoneBlock;
import net.blay09.mods.waystones.tileentity.WaystoneTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderWaystone extends TileEntityRenderer<WaystoneTileEntity> {

    private static final ResourceLocation texture = new ResourceLocation(Waystones.MOD_ID, "textures/entity/waystone.png");
    private static final ResourceLocation textureMossy = new ResourceLocation(Waystones.MOD_ID, "textures/entity/waystone_mossy.png");
    private static final ResourceLocation textureActive = new ResourceLocation(Waystones.MOD_ID, "textures/entity/waystone_active.png");

    private final ModelWaystone model = new ModelWaystone();

    @Override
    public void render(WaystoneTileEntity tileEntity, double x, double y, double z, float partialTicks, int destroyStage) {
        BlockState state = (tileEntity != null && tileEntity.hasWorld()) ? tileEntity.getWorld().getBlockState(tileEntity.getPos()) : null;
        if (state != null && state.getBlock() != ModBlocks.waystone) { // I don't know. But it seems for some reason the renderer gets called for minecraft:air in certain cases.
            return;
        }

        boolean isDummy = state != null && state.get(WaystoneBlock.HALF) == DoubleBlockHalf.UPPER;
        if (isDummy && destroyStage < 0) {
            return;
        }

        if (destroyStage >= 0) {
            bindTexture(DESTROY_STAGES[destroyStage]);
            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(4f, 8f, 1f);
            GlStateManager.translatef(0.0625f, 0.0625f, 0.0625f);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        } else {
            boolean isMossy = tileEntity != null && tileEntity.isMossy() && WaystoneConfig.CLIENT.randomlySpawnedLookMossy.get();
            bindTexture(isMossy ? textureMossy : texture);
        }

        float angle = state != null ? state.get(WaystoneBlock.FACING).getHorizontalAngle() : 0f;
        GlStateManager.pushMatrix();
//		GlStateManager.enableLighting();
        GlStateManager.color4f(1f, 1f, 1f, 1f);
        GlStateManager.translated(x + 0.5, y + (isDummy ? -1 : 0), z + 0.5);
        GlStateManager.rotatef(angle, 0f, 1f, 0f);
        GlStateManager.rotatef(-180f, 1f, 0f, 0f);
        GlStateManager.scalef(0.5f, 0.5f, 0.5f);
        model.renderAll();
        boolean isActivated = false; // TODO (ClientWaystones.getKnownWaystone(tileEntity.getWaystoneName()) != null);
        if (tileEntity != null && tileEntity.hasWorld() && isActivated) {
            bindTexture(textureActive);
            GlStateManager.scalef(1.05f, 1.05f, 1.05f);
            if (!WaystoneConfig.CLIENT.disableTextGlow.get()) {
//				GlStateManager.disableLighting();
                Minecraft.getInstance().gameRenderer.disableLightmap();
            }
            model.renderPillar();
            if (!WaystoneConfig.CLIENT.disableTextGlow.get()) {
                Minecraft.getInstance().gameRenderer.enableLightmap();
            }
        }
        GlStateManager.popMatrix();
//		GlStateManager.color(1f, 1f, 1f, 1f);

        if (destroyStage >= 0) {
            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        }
    }
}
