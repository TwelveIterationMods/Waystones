package net.blay09.mods.waystones.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class SharestoneModel extends Model {

    private ModelRenderer pillar;

    public SharestoneModel() {
        super(it -> RenderType.getCutout());
        textureWidth = 256;
        textureHeight = 256;

        pillar = new ModelRenderer(this, 0, 0);
        pillar.addBox(-14f, 6f, -14f, 28, 52, 28);
    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        pillar.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
