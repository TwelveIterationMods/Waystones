package net.blay09.mods.waystones.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ModelWaystone extends Model {

    private ModelRenderer pillar;

    public ModelWaystone() {
        super(it -> RenderType.cutout());
        textureWidth = 256;
        textureHeight = 256;

        pillar = new ModelRenderer(this, 144, 0);
        pillar.addBox(-10f, -48f, -10f, 20, 28, 20);
    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        pillar.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
