package net.blay09.mods.waystones.api.requirement;

import net.blay09.mods.waystones.api.WaystoneTeleportContext;
import net.minecraft.resources.ResourceLocation;

public interface ConditionResolver<P> {
    ResourceLocation getId();

    Class<P> getParameterType();

    boolean matches(WaystoneTeleportContext context, P parameters);
}
