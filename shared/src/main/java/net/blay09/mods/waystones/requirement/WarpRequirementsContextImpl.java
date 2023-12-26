package net.blay09.mods.waystones.requirement;

import net.blay09.mods.waystones.api.WaystoneTeleportContext;
import net.blay09.mods.waystones.api.requirement.WarpRequirement;
import net.blay09.mods.waystones.api.requirement.WarpRequirementsContext;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class WarpRequirementsContextImpl implements WarpRequirementsContext {

    private final Map<ResourceLocation, WarpRequirement> requirements = new HashMap<>();
    private final WaystoneTeleportContext context;

    public WarpRequirementsContextImpl(WaystoneTeleportContext context) {
        this.context = context;
    }

    @SuppressWarnings("unchecked")
    public <T extends WarpRequirement, P> void apply(WarpModifierRegistry.ConfiguredRequirementModifier<T, P> configuredModifier) {
        for (final var condition : configuredModifier.conditions()) {
            if (!matchesCondition(condition)) {
                return;
            }
        }

        final var modifier = configuredModifier.modifier();
        final var parameters = configuredModifier.parameters();
        var requirement = (T) requirements.get(modifier.getRequirementType());
        if (requirement == null) {
            requirement = WarpModifierRegistry.<T>getRequirementType(modifier.getRequirementType()).createInstance();
        }
        requirements.put(modifier.getRequirementType(), modifier.apply(requirement, this, parameters));
    }

    public float getContextValue(ResourceLocation id) {
        final var resolver = WarpModifierRegistry.getVariableResolver(id);
        if (resolver != null) {
            return resolver.resolve(context);
        }

        return 0f;
    }

    public boolean matchesCondition(ResourceLocation id) {
        final var resolver = WarpModifierRegistry.getConditionResolver(id);
        if (resolver != null) {
            return resolver.matches(context);
        }

        return false;
    }

    public WarpRequirement resolve() {
        if (requirements.isEmpty()) {
            return NoRequirement.INSTANCE;
        } else if (requirements.size() == 1) {
            return requirements.values().iterator().next();
        }
        return new CombinedWarpRequirement(requirements.values());
    }
}
