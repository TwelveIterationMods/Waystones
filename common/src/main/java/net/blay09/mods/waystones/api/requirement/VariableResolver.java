package net.blay09.mods.waystones.api.requirement;

import net.blay09.mods.waystones.api.WaystoneTeleportContext;
import net.minecraft.resources.ResourceLocation;

public interface VariableResolver {
    ResourceLocation getId();
    float resolve(WaystoneTeleportContext context);
}
