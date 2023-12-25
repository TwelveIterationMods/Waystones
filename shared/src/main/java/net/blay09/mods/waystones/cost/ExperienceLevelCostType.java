package net.blay09.mods.waystones.cost;

import net.blay09.mods.waystones.api.cost.CostType;
import net.blay09.mods.waystones.xp.ExperienceLevelCost;
import net.minecraft.resources.ResourceLocation;

public class ExperienceLevelCostType implements CostType<ExperienceLevelCost> {

    public static final ResourceLocation ID = new ResourceLocation("waystones", "experience_levels");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public ExperienceLevelCost createInstance() {
        return new ExperienceLevelCost(0);
    }
}
