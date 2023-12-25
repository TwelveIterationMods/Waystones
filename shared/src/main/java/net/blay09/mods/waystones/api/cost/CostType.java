package net.blay09.mods.waystones.api.cost;

import net.minecraft.resources.ResourceLocation;

public interface CostType<T extends Cost> {
    ResourceLocation getId();
    T createInstance();
}
