package net.blay09.mods.waystones.api.requirement;

import net.blay09.mods.waystones.api.WaystoneTeleportContext;
import net.minecraft.resources.ResourceLocation;

public interface ConditionResolver {
    ResourceLocation getId();
    boolean matches(WaystoneTeleportContext context);
}
