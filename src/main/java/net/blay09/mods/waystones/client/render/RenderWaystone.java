package net.blay09.mods.waystones.client.render;

import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.TileWaystone;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

public class RenderWaystone extends TileEntitySpecialRenderer {

	private static final ResourceLocation texture = new ResourceLocation(Waystones.MOD_ID, "textures/entity/waystone.png");
	private static final ResourceLocation textureActive = new ResourceLocation(Waystones.MOD_ID, "textures/entity/waystone_active.png");

	private final ModelWaystone model = new ModelWaystone();

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTicks) {
		TileWaystone tileWaystone = (TileWaystone) tileEntity;
		bindTexture(texture);

		float angle = tileEntity.hasWorldObj() ? WaystoneManager.getRotationYaw(ForgeDirection.getOrientation(tileEntity.getBlockMetadata())) : 0f;
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glColor4f(1f, 1f, 1f, 1f);
		GL11.glTranslated(x + 0.5, y, z + 0.5);
		GL11.glRotatef(angle, 0f, 1f, 0f);
		GL11.glRotatef(-180f, 1f, 0f, 0f);
		GL11.glScalef(0.5f, 0.5f, 0.5f);
		model.renderAll();
		if(tileWaystone.hasWorldObj() && WaystoneManager.getKnownWaystone(tileWaystone.getWaystoneName()) != null || WaystoneManager.getServerWaystone(tileWaystone.getWaystoneName()) != null) {
			bindTexture(textureActive);
			GL11.glScalef(1.05f, 1.05f, 1.05f);
			if(!WaystoneConfig.disableTextGlow) {
				GL11.glDisable(GL11.GL_LIGHTING);
				Minecraft.getMinecraft().entityRenderer.disableLightmap(0);
			}
			model.renderPillar();
			if(!WaystoneConfig.disableTextGlow) {
				Minecraft.getMinecraft().entityRenderer.enableLightmap(0);
			}
		}
		GL11.glPopMatrix();
	}
}
