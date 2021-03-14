package net.blay09.mods.waystones.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.WaystoneBlock;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.tileentity.WaystoneTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;

import java.util.Objects;

public class WaystoneRenderer extends TileEntityRenderer<WaystoneTileEntity> {

    private static final ModelWaystone model = new ModelWaystone();
    private static final RenderMaterial MATERIAL = new RenderMaterial(Atlases.SIGN_ATLAS, new ResourceLocation(Waystones.MOD_ID, "entity/waystone_active"));

    public WaystoneRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(WaystoneTileEntity tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        BlockState state = tileEntity.getBlockState();
        if (state.get(WaystoneBlock.HALF) != DoubleBlockHalf.LOWER) {
            return;
        }

        float angle = state.get(WaystoneBlock.FACING).getHorizontalAngle();
        matrixStack.push();
        matrixStack.translate(0.5f, 0f, 0.5f);
        matrixStack.rotate(new Quaternion(0f, angle, 0f, true));
        matrixStack.rotate(new Quaternion(-180f, 0f, 0f, true));
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        PlayerEntity player = Minecraft.getInstance().player;
        boolean isActivated = PlayerWaystoneManager.isWaystoneActivated(Objects.requireNonNull(player), tileEntity.getWaystone());
        if (isActivated) {
            matrixStack.scale(1.05f, 1.05f, 1.05f);
            IVertexBuilder vertexBuilder = MATERIAL.getBuffer(buffer, RenderType::getEntityCutout);
            int light = WaystonesConfig.CLIENT.disableTextGlow.get() ? combinedLightIn : 15728880;
            int overlay = WaystonesConfig.CLIENT.disableTextGlow.get() ? combinedOverlayIn : OverlayTexture.NO_OVERLAY;
            model.render(matrixStack, vertexBuilder, light, overlay, 1f, 1f, 1f, 1f);
        }
        matrixStack.pop();
    }
}
