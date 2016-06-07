package net.blay09.mods.waystones.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelWaystone extends ModelBase {

	private ModelRenderer top;
	private ModelRenderer topMidTop;
	private ModelRenderer pillar;
	private ModelRenderer topBottom;
	private ModelRenderer baseTop;
	private ModelRenderer topMidBottom;
	private ModelRenderer baseMid;
	private ModelRenderer baseBottom;

	public ModelWaystone() {
		textureWidth = 256;
		textureHeight = 256;

		top = new ModelRenderer(this, 0, 0);
		top.addBox(-8f, -64f, -8f, 16, 4, 16);

		topMidTop = new ModelRenderer(this, 64, 0);
		topMidTop.addBox(-10f, -60f, -10f, 20, 4, 20);

		topMidBottom = new ModelRenderer(this, 0, 76);
		topMidBottom.addBox(-14f, -56f, -14f, 28, 4, 28);

		topBottom = new ModelRenderer(this, 0, 24);
		topBottom.addBox(-12f, -52f, -12f, 24, 4, 24);

		pillar = new ModelRenderer(this, 144, 0);
		pillar.addBox(-10f, -48f, -10f, 20, 28, 20);

		baseTop = new ModelRenderer(this, 96, 48);
		baseTop.addBox(-12f, -20f, -12f, 24, 4, 24);

		baseMid = new ModelRenderer(this, 112, 76);
		baseMid.addBox(-14f, -16f, -14f, 28, 8, 28);

		baseBottom = new ModelRenderer(this, 0, 112);
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
