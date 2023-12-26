package net.blay09.mods.waystones.requirement;

import net.blay09.mods.waystones.api.requirement.RequirementFunction;
import net.blay09.mods.waystones.api.requirement.WarpRequirement;

public record ConfiguredRequirement<T extends WarpRequirement, P>(RequirementFunction<T, P> modifier, P parameters) {
}
