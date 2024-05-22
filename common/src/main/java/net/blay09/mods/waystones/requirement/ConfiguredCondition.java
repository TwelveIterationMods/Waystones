package net.blay09.mods.waystones.requirement;

import net.blay09.mods.waystones.api.requirement.ConditionResolver;

public record ConfiguredCondition<P>(ConditionResolver<P> resolver, P parameters) {
}
