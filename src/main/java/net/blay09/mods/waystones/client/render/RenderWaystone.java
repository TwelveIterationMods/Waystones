package net.blay09.mods.waystones.client.render;

import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.BlockWaystone;
import net.blay09.mods.waystones.block.TileWaystone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderWaystone extends TileEntitySpecialRenderer<TileWaystone> {

	private static final ResourceLocation texture = new ResourceLocation(Waystones.MOD_ID, "textures/entity/waystone.png");
	private static final ResourceLocation textureActive = new ResourceLocation(Waystones.MOD_ID, "textures/entity/waystone_active.png");

	private final ModelWaystone model = new ModelWaystone();

	@Override
	public void renderTileEntityAt(TileWaystone tileEntity, double x, double y, double z, float partialTicks, int destroyStage) {
		IBlockState state = (tileEntity != null && tileEntity.hasWorld()) ? tileEntity.getWorld().getBlockState(tileEntity.getPos()) : null;
		if(state != null && state.getBlock() != Waystones.blockWaystone) { // I don't know. But it seems for some reason the renderer gets called for minecraft:air in certain cases.
			return;
		}

		bindTexture(texture);

		float angle = state != null ? WaystoneManager.getRotationYaw(state.getValue(BlockWaystone.FACING)) : 0f;
		GlStateManager.pushMatrix();
//		GlStateManager.enableLighting();
		GlStateManager.color(1f, 1f, 1f, 1f);
		GlStateManager.translate(x + 0.5, y, z + 0.5);
		GlStateManager.rotate(angle, 0f, 1f, 0f);
		GlStateManager.rotate(-180f, 1f, 0f, 0f);
		GlStateManager.scale(0.5f, 0.5f, 0.5f);
		model.renderAll();
		if(tileEntity != null && tileEntity.hasWorld() && (WaystoneManager.getKnownWaystone(tileEntity.getWaystoneName()) != null || WaystoneManager.getServerWaystone(tileEntity.getWaystoneName()) != null)) {
			bindTexture(textureActive);
			GlStateManager.scale(1.05f, 1.05f, 1.05f);
			if(!WaystoneConfig.disableTextGlow) {
//				GlStateManager.disableLighting();
				Minecraft.getMinecraft().entityRenderer.disableLightmap();
			}
			model.renderPillar();
			if(!WaystoneConfig.disableTextGlow) {
				Minecraft.getMinecraft().entityRenderer.enableLightmap();
			}
		}
		GlStateManager.popMatrix();
	}
}
