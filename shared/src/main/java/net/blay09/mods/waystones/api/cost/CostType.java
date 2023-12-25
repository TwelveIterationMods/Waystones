package net.blay09.mods.waystones.api.cost;

import net.minecraft.resources.ResourceLocation;

public interface CostType<T> {
    ResourceLocation getId();
    T createInstance();
}
