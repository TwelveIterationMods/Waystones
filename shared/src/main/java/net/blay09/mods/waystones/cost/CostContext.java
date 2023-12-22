package net.blay09.mods.waystones.cost;

import net.blay09.mods.waystones.api.IWaystoneTeleportContext;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class CostContext {

    private final Map<ResourceLocation, Object> costInstances = new HashMap<>();
    private final IWaystoneTeleportContext context;

    public CostContext(IWaystoneTeleportContext context) {
        this.context = context;
    }

    @SuppressWarnings("unchecked")
    public <T, P> void apply(CostModifier<T, P> modifier) {
        var costInstance = (T) costInstances.get(modifier.getCostType());
        if (costInstance == null) {
            costInstance = CostRegistry.<T>getCostType(modifier.getCostType()).createInstance();
        }
        costInstances.put(modifier.getCostType(), modifier.apply(costInstance, this, null));
    }

    public float getContextValue(ResourceLocation id) {
        return switch (id.toString()) {
            case "waystones:distance" -> (float) Math.sqrt(context.getEntity().distanceToSqr(context.getDestination().getLocation()));
            default -> 0f;
        };
    }

    public boolean matchesCondition(ResourceLocation id) {

        return false;
    }
}
