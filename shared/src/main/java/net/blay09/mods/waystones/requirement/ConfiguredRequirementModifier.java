package net.blay09.mods.waystones.requirement;

import net.blay09.mods.waystones.api.requirement.WarpRequirement;
import net.blay09.mods.waystones.api.requirement.WarpRequirementModifier;

import java.util.List;

public record ConfiguredRequirementModifier<T extends WarpRequirement, P>(WarpRequirementModifier<T, P> modifier, List<ConfiguredCondition<?>> conditions,
                                                                          P parameters) {
}
