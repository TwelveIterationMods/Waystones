package net.blay09.mods.waystones.cost;

import net.blay09.mods.waystones.api.WaystoneCooldowns;
import net.blay09.mods.waystones.api.cost.CostType;
import net.minecraft.resources.ResourceLocation;

public class CooldownCostType implements CostType<CooldownCost> {

    public static final ResourceLocation ID = new ResourceLocation("waystones", "cooldown");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public CooldownCost createInstance() {
        return new CooldownCost(WaystoneCooldowns.INVENTORY_BUTTON, 0);
    }
}
