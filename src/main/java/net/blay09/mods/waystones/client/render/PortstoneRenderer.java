package net.blay09.mods.waystones.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.PortstoneBlock;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.item.ModItems;
import net.blay09.mods.waystones.tileentity.PortstoneTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.world.World;

public class PortstoneRenderer extends TileEntityRenderer<PortstoneTileEntity> {

    private static final PortstoneModel model = new PortstoneModel();
    private static final RenderMaterial MATERIAL = new RenderMaterial(Atlases.SIGN_ATLAS, new ResourceLocation(Waystones.MOD_ID, "entity/portstone"));

    private static ItemStack warpStoneItem;

    public PortstoneRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(PortstoneTileEntity tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        World world = tileEntity.getWorld();
        BlockState state = tileEntity.getBlockState();
        if (world == null || state.get(PortstoneBlock.HALF) != DoubleBlockHalf.LOWER) {
            return;
        }
        Direction facing = state.get(PortstoneBlock.FACING);

        if (warpStoneItem == null) {
            warpStoneItem = new ItemStack(ModItems.warpStone);
            warpStoneItem.addEnchantment(Enchantments.UNBREAKING, 1);
        }

        matrixStack.push();
        matrixStack.translate(0.5f, 0f, 0.5f);
        matrixStack.rotate(new Quaternion(0f, -facing.getHorizontalAngle(), 0f, true));
        matrixStack.rotate(new Quaternion(-180f, 0f, 0f, true));
        matrixStack.translate(0f, -2f, 0f);
        float scale = 1.01f;
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        matrixStack.scale(scale, scale, scale);
        PortstoneModel model = new PortstoneModel();
        IVertexBuilder vertexBuilder = MATERIAL.getBuffer(buffer, RenderType::getEntityCutout);
        int light = WaystonesConfig.CLIENT.disableTextGlow.get() ? combinedLightIn : 15728880;
        int overlay = WaystonesConfig.CLIENT.disableTextGlow.get() ? combinedOverlayIn : OverlayTexture.NO_OVERLAY;
        long gameTime = world.getGameTime();
        float min = 0.7f;
        float color = (float) Math.max(min, min + Math.abs(Math.sin(gameTime / 32f)) * (1f - min));
        model.render(matrixStack, vertexBuilder, light, overlay, color, color, color, 1f);
        matrixStack.pop();

        matrixStack.push();
        matrixStack.translate(0.5f, 1f, 0.5f);
        matrixStack.rotate(new Quaternion(0f, -facing.getHorizontalAngle(), 0f, true));
        matrixStack.translate(0f, 0f, 0.15f);
        matrixStack.rotate(new Quaternion(-25f, 0f, 0f, true));
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        matrixStack.translate(0.03125f, 0f, 0f);
        Minecraft.getInstance().getItemRenderer().renderItem(warpStoneItem, ItemCameraTransforms.TransformType.FIXED, combinedLightIn, combinedOverlayIn, matrixStack, buffer);
        matrixStack.pop();
    }
}
