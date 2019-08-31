package net.blay09.mods.waystones.client.render;

import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.WaystoneBlock;
import net.blay09.mods.waystones.tileentity.WaystoneTileEntity;
import net.blay09.mods.waystones.client.ClientWaystones;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderWaystone extends TileEntitySpecialRenderer<WaystoneTileEntity> {

    private static final ResourceLocation texture = new ResourceLocation(Waystones.MOD_ID, "textures/entity/waystone.png");
    private static final ResourceLocation textureMossy = new ResourceLocation(Waystones.MOD_ID, "textures/entity/waystone_mossy.png");
    private static final ResourceLocation textureActive = new ResourceLocation(Waystones.MOD_ID, "textures/entity/waystone_active.png");

    private final ModelWaystone model = new ModelWaystone();

    @Override
    public void render(WaystoneTileEntity tileEntity, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        IBlockState state = (tileEntity != null && tileEntity.hasWorld()) ? tileEntity.getWorld().getBlockState(tileEntity.getPos()) : null;
        if (state != null && state.getBlock() != Waystones.blockWaystone) { // I don't know. But it seems for some reason the renderer gets called for minecraft:air in certain cases.
            return;
        }

        boolean isDummy = state != null && !state.getValue(WaystoneBlock.BASE);
        if (isDummy && destroyStage < 0) {
            return;
        }

        if (destroyStage >= 0) {
            bindTexture(DESTROY_STAGES[destroyStage]);
            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.scale(4f, 8f, 1f);
            GlStateManager.translate(0.0625f, 0.0625f, 0.0625f);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        } else {
            boolean isMossy = tileEntity != null && tileEntity.isMossy() && WaystoneConfig.client.randomlySpawnedLookMossy;
            bindTexture(isMossy ? textureMossy : texture);
        }

        float angle = state != null ? WaystoneManager.getRotationYaw(state.getValue(WaystoneBlock.FACING)) : 0f;
        GlStateManager.pushMatrix();
//		GlStateManager.enableLighting();
        GlStateManager.color(1f, 1f, 1f, 1f);
        GlStateManager.translate(x + 0.5, y + (isDummy ? -1 : 0), z + 0.5);
        GlStateManager.rotate(angle, 0f, 1f, 0f);
        GlStateManager.rotate(-180f, 1f, 0f, 0f);
        GlStateManager.scale(0.5f, 0.5f, 0.5f);
        model.renderAll();
        if (tileEntity != null && tileEntity.hasWorld() && (ClientWaystones.getKnownWaystone(tileEntity.getWaystoneName()) != null)) {
            bindTexture(textureActive);
            GlStateManager.scale(1.05f, 1.05f, 1.05f);
            if (!WaystoneConfig.client.disableTextGlow) {
//				GlStateManager.disableLighting();
                Minecraft.getMinecraft().entityRenderer.disableLightmap();
            }
            model.renderPillar();
            if (!WaystoneConfig.client.disableTextGlow) {
                Minecraft.getMinecraft().entityRenderer.enableLightmap();
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
