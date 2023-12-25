package net.blay09.mods.waystones.api.cost;

import net.minecraft.resources.ResourceLocation;

public interface CostModifier<TCost, TParameter> extends CostModifierFunction<TCost, TParameter> {
    ResourceLocation getId();

    ResourceLocation getCostType();

    Class<TParameter> getParameterType();
}

