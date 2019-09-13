package net.blay09.mods.waystones.client.render;

import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;

public class ModelWaystone extends Model {

    private RendererModel top;
    private RendererModel topMidTop;
    private RendererModel pillar;
    private RendererModel topBottom;
    private RendererModel baseTop;
    private RendererModel topMidBottom;
    private RendererModel baseMid;
    private RendererModel baseBottom;

    public ModelWaystone() {
        textureWidth = 256;
        textureHeight = 256;

        top = new RendererModel(this, 0, 0);
        top.addBox(-8f, -64f, -8f, 16, 4, 16);

        topMidTop = new RendererModel(this, 64, 0);
        topMidTop.addBox(-10f, -60f, -10f, 20, 4, 20);

        topMidBottom = new RendererModel(this, 0, 76);
        topMidBottom.addBox(-14f, -56f, -14f, 28, 4, 28);

        topBottom = new RendererModel(this, 0, 24);
        topBottom.addBox(-12f, -52f, -12f, 24, 4, 24);

        pillar = new RendererModel(this, 144, 0);
        pillar.addBox(-10f, -48f, -10f, 20, 28, 20);

        baseTop = new RendererModel(this, 96, 48);
        baseTop.addBox(-12f, -20f, -12f, 24, 4, 24);

        baseMid = new RendererModel(this, 112, 76);
        baseMid.addBox(-14f, -16f, -14f, 28, 8, 28);

        baseBottom = new RendererModel(this, 0, 112);
        baseBottom.addBox(-16f, -8f, -16f, 32, 8, 32);
    }

    public void renderAll() {
        float f = 0.0625f;
        top.render(f);
        topMidTop.render(f);
        topMidBottom.render(f);
        topBottom.render(f);
        pillar.render(f);
        baseTop.render(f);
        baseMid.render(f);
        baseBottom.render(f);
    }

    public void renderPillar() {
        float f = 0.0625f;
        pillar.render(f);
    }
}
