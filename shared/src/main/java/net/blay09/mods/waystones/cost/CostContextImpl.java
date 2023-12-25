package net.blay09.mods.waystones.cost;

import com.mojang.datafixers.util.Pair;
import net.blay09.mods.waystones.api.IWaystoneTeleportContext;
import net.blay09.mods.waystones.api.cost.Cost;
import net.blay09.mods.waystones.api.cost.CostContext;
import net.blay09.mods.waystones.api.cost.CostModifier;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class CostContextImpl implements CostContext {

    private final Map<ResourceLocation, Cost> costInstances = new HashMap<>();
    private final IWaystoneTeleportContext context;

    public CostContextImpl(IWaystoneTeleportContext context) {
        this.context = context;
    }

    public <T extends Cost, P> void apply(Pair<CostModifier<T, P>, P> modifierAndParameters) {
        applyModifier(modifierAndParameters.getFirst(), modifierAndParameters.getSecond());
    }

    @SuppressWarnings("unchecked")
    public <T extends Cost, P> void applyModifier(CostModifier<T, P> modifier, P parameters) {
        var costInstance = (T) costInstances.get(modifier.getCostType());
        if (costInstance == null) {
            costInstance = CostRegistry.<T>getCostType(modifier.getCostType()).createInstance();
        }
        costInstances.put(modifier.getCostType(), modifier.apply(costInstance, this, parameters));
    }

    public float getContextValue(ResourceLocation id) {
        final var resolver = CostRegistry.getVariableResolver(id);
        if (resolver != null) {
            return resolver.resolve(context);
        }

        return 0f;
    }

    public boolean matchesCondition(ResourceLocation id) {
        final var resolver = CostRegistry.getConditionResolver(id);
        if (resolver != null) {
            return resolver.matches(context);
        }

        return false;
    }

    public Cost resolve() {
        if (costInstances.isEmpty()) {
            return NoCost.INSTANCE;
        } else if (costInstances.size() == 1) {
            return costInstances.values().iterator().next();
        }
        return new CombinedCost(costInstances.values());
    }
}
