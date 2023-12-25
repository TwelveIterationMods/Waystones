package net.blay09.mods.waystones.api.cost;

import net.minecraft.resources.ResourceLocation;

public interface CostContext {
    boolean matchesCondition(ResourceLocation id);

    float getContextValue(ResourceLocation id);
}
