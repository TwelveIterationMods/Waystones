package net.blay09.mods.waystones.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.PortstoneBlock;
import net.blay09.mods.waystones.block.entity.PortstoneBlockEntity;
import net.blay09.mods.waystones.client.ModRenderers;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class PortstoneRenderer implements BlockEntityRenderer<PortstoneBlockEntity> {
    private static final Material MATERIAL = new Material(Sheets.SIGN_SHEET, new ResourceLocation(Waystones.MOD_ID, "entity/portstone"));
    private static ItemStack warpStoneItem;

    private final PortstoneModel model;

    public PortstoneRenderer(BlockEntityRendererProvider.Context context) {
        model = new PortstoneModel(context.bakeLayer(ModRenderers.portstoneModel));
    }

    @Override
    public void render(PortstoneBlockEntity tileEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        Level world = tileEntity.getLevel();
        BlockState state = tileEntity.getBlockState();
        if (world == null || state.getValue(PortstoneBlock.HALF) != DoubleBlockHalf.LOWER) {
            return;
        }
        Direction facing = state.getValue(PortstoneBlock.FACING);

        if (warpStoneItem == null) {
            warpStoneItem = new ItemStack(ModItems.warpStone);
            warpStoneItem.enchant(Enchantments.UNBREAKING, 1);
        }

        matrixStack.pushPose();
        matrixStack.translate(0.5f, 0f, 0.5f);
        matrixStack.mulPose(new Quaternion(0f, -facing.toYRot(), 0f, true));
        matrixStack.mulPose(new Quaternion(-180f, 0f, 0f, true));
        matrixStack.translate(0f, -2f, 0f);
        float scale = 1.01f;
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        matrixStack.scale(scale, scale, scale);

        VertexConsumer vertexBuilder = MATERIAL.buffer(buffer, RenderType::entityCutout);
        int light = WaystonesConfig.getActive().disableTextGlow() ? combinedLightIn : 15728880;
        int overlay = WaystonesConfig.getActive().disableTextGlow() ? combinedOverlayIn : OverlayTexture.NO_OVERLAY;
        long gameTime = world.getGameTime();
        float min = 0.7f;
        float color = (float) Math.max(min, min + Math.abs(Math.sin(gameTime / 32f)) * (1f - min));
        model.renderToBuffer(matrixStack, vertexBuilder, light, overlay, color, color, color, 1f);
        matrixStack.popPose();

        matrixStack.pushPose();
        matrixStack.translate(0.5f, 1f, 0.5f);
        matrixStack.mulPose(new Quaternion(0f, -facing.toYRot(), 0f, true));
        matrixStack.translate(0f, 0f, 0.15f);
        matrixStack.mulPose(new Quaternion(-25f, 0f, 0f, true));
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        matrixStack.translate(0.03125f, 0f, 0f);
        Minecraft.getInstance().getItemRenderer().renderStatic(warpStoneItem, ItemTransforms.TransformType.FIXED, combinedLightIn, combinedOverlayIn, matrixStack, buffer, 0);
        matrixStack.popPose();
    }
}
