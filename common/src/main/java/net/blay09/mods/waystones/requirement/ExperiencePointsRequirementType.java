package net.blay09.mods.waystones.requirement;

import net.blay09.mods.waystones.api.requirement.RequirementType;
import net.minecraft.resources.ResourceLocation;

public class ExperiencePointsRequirementType implements RequirementType<ExperiencePointsRequirement> {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("waystones", "experience_points");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public ExperiencePointsRequirement createInstance() {
        return new ExperiencePointsRequirement(0);
    }
}
