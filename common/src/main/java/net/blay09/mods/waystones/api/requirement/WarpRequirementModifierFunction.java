package net.blay09.mods.waystones.api.requirement;

public interface WarpRequirementModifierFunction<TRequirement, TParameter> {
    TRequirement apply(TRequirement requirement, WarpRequirementsContext context, TParameter parameters);
}
