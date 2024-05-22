package net.blay09.mods.waystones.api.requirement;

import net.minecraft.resources.ResourceLocation;

public interface RequirementType<T extends WarpRequirement> {
    ResourceLocation getId();
    T createInstance();
}
