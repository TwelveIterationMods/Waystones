package net.blay09.mods.waystones.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.blay09.mods.waystones.block.PortstoneBlock;
import net.blay09.mods.waystones.item.ModItems;
import net.blay09.mods.waystones.tileentity.PortstoneTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.world.World;

public class PortstoneRenderer extends TileEntityRenderer<PortstoneTileEntity> {

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
