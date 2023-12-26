package net.blay09.mods.waystones.requirement;

import net.blay09.mods.waystones.api.requirement.RequirementType;
import net.minecraft.resources.ResourceLocation;

public class ExperienceLevelRequirementType implements RequirementType<ExperienceLevelRequirement> {

    public static final ResourceLocation ID = new ResourceLocation("waystones", "experience_levels");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public ExperienceLevelRequirement createInstance() {
        return new ExperienceLevelRequirement(0);
    }
}
