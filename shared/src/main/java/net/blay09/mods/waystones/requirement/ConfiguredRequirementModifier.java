package net.blay09.mods.waystones.requirement;

import net.blay09.mods.waystones.api.requirement.WarpRequirement;

import java.util.List;

public record ConfiguredRequirementModifier<T extends WarpRequirement, P>(ConfiguredRequirement<T, P> requirement, List<ConfiguredCondition<?>> conditions) {
}
