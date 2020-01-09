package net.blay09.mods.waystones.client.render;

import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;

public class ModelWaystone extends Model {

    private RendererModel pillar;

    public ModelWaystone() {
        textureWidth = 256;
        textureHeight = 256;

        pillar = new RendererModel(this, 144, 0);
        pillar.addBox(-10f, -48f, -10f, 20, 28, 20);
    }

    public void renderPillar() {
        float f = 0.0625f;
        pillar.render(f);
    }
}
