package net.blay09.mods.waystones.cost;

public interface CostModifierFunction<TCost, TParameter> {
    TCost apply(TCost costInstance, CostContext context, TParameter parameters);
}
