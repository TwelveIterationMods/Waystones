package net.blay09.mods.waystones.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.SharestoneBlock;
import net.blay09.mods.waystones.block.entity.SharestoneBlockEntity;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class SharestoneRenderer implements BlockEntityRenderer<SharestoneBlockEntity> {

    private static final Material MATERIAL = new Material(Sheets.SIGN_SHEET, new ResourceLocation(Waystones.MOD_ID, "entity/sharestone_color"));

    private static ItemStack warpStoneItem;

    private final SharestoneModel model;

    public SharestoneRenderer(BlockEntityRendererProvider.Context context) {
        model = new SharestoneModel(context.bakeLayer(ModRenderers.sharestoneModel));
    }

    @Override
    public void render(SharestoneBlockEntity tileEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        Level world = tileEntity.getLevel();
        BlockState state = tileEntity.getBlockState();
        if (world == null || state.getValue(SharestoneBlock.HALF) != DoubleBlockHalf.LOWER) {
            return;
        }

        long gameTime = world.getGameTime();

        DyeColor color = ((SharestoneBlock) state.getBlock()).getColor();
        if (color != null) {
            matrixStack.pushPose();
            matrixStack.translate(0.5f, 0f, 0.5f);
            matrixStack.mulPose(new Quaternion(-180f, 0f, 0f, true));
            matrixStack.translate(0f, -2f, 0f);
            float scale = 1.01f;
            matrixStack.scale(0.5f, 0.5f, 0.5f);
            matrixStack.scale(scale, scale, scale);
            VertexConsumer vertexBuilder = MATERIAL.buffer(buffer, RenderType::entityCutout);
            int light = WaystonesConfig.getActive().disableTextGlow() ? combinedLightIn : 15728880;
            int overlay = WaystonesConfig.getActive().disableTextGlow() ? combinedOverlayIn : OverlayTexture.NO_OVERLAY;
            float[] colors = color.getTextureDiffuseColors();
            model.renderToBuffer(matrixStack, vertexBuilder, light, overlay, colors[0], colors[1], colors[2], 1f);
            matrixStack.popPose();
        }

        if (warpStoneItem == null) {
            warpStoneItem = new ItemStack(ModItems.warpStone);
            warpStoneItem.enchant(Enchantments.UNBREAKING, 1);
        }

        float angle = gameTime / 2f % 360;
        float offsetY = (float) Math.sin(gameTime / 8f) * 0.025f;
        matrixStack.pushPose();
        matrixStack.translate(0.5f, 1f + offsetY, 0.5f);
        matrixStack.mulPose(new Quaternion(0f, angle, 0f, true));
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        Minecraft.getInstance().getItemRenderer().renderStatic(warpStoneItem, ItemTransforms.TransformType.FIXED, combinedLightIn, combinedOverlayIn, matrixStack, buffer, 0);
        matrixStack.popPose();
    }
}
