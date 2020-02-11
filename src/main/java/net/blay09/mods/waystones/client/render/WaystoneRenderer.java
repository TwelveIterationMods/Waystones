package net.blay09.mods.waystones.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.WaystoneBlock;
import net.blay09.mods.waystones.config.WaystoneConfig;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.tileentity.WaystoneTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

public class WaystoneRenderer extends TileEntityRenderer<WaystoneTileEntity> {

    private static final ResourceLocation textureActive = new ResourceLocation(Waystones.MOD_ID, "textures/entity/waystone_active.png");

    private final ModelWaystone model = new ModelWaystone();

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
            if (!WaystoneConfig.CLIENT.disableTextGlow.get()) {
                // TODO Minecraft.getInstance().gameRenderer.disableLightmap();
            }
            model.render(matrixStack, buffer.getBuffer(RenderType.cutout()), combinedLightIn, combinedOverlayIn, 1f, 1f, 1f, 1f);
            if (!WaystoneConfig.CLIENT.disableTextGlow.get()) {
                // TODO Minecraft.getInstance().gameRenderer.enableLightmap();
            }
        }
        matrixStack.pop();
    }
}
