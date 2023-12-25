package net.blay09.mods.waystones.cost;

import net.blay09.mods.waystones.api.cost.CostType;
import net.blay09.mods.waystones.xp.ExperiencePointsCost;
import net.minecraft.resources.ResourceLocation;

public class ExperiencePointsCostType implements CostType<ExperiencePointsCost> {

    public static final ResourceLocation ID = new ResourceLocation("waystones", "experience_points");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public ExperiencePointsCost createInstance() {
        return new ExperiencePointsCost(0);
    }
}
