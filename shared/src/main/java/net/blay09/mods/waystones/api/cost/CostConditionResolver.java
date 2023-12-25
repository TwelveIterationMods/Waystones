package net.blay09.mods.waystones.api.cost;

import net.blay09.mods.waystones.api.IWaystoneTeleportContext;
import net.minecraft.resources.ResourceLocation;

public interface CostConditionResolver {
    ResourceLocation getId();
    boolean matches(IWaystoneTeleportContext context);
}
