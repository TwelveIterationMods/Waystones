package net.blay09.mods.waystones.api.requirement;

import net.minecraft.resources.ResourceLocation;

public interface WarpRequirementModifier<TRequirement extends WarpRequirement, TParameter> extends WarpRequirementModifierFunction<TRequirement, TParameter> {
    ResourceLocation getId();

    ResourceLocation getRequirementType();

    Class<TParameter> getParameterType();

    boolean isEnabled();
}

