package net.blay09.mods.waystones.api.requirement;

import net.minecraft.resources.ResourceLocation;

public interface WarpRequirementsContext {
    boolean matchesCondition(ResourceLocation id);

    float getContextValue(ResourceLocation id);
}
