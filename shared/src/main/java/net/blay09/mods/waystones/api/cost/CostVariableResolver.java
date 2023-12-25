package net.blay09.mods.waystones.api.cost;

import net.blay09.mods.waystones.api.WaystoneTeleportContext;
import net.minecraft.resources.ResourceLocation;

public interface CostVariableResolver {
    ResourceLocation getId();
    float resolve(WaystoneTeleportContext context);
}
