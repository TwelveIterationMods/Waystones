package net.blay09.mods.waystones.api.client;

import net.blay09.mods.waystones.api.requirement.WarpRequirement;
import net.blay09.mods.waystones.client.requirement.RequirementRenderer;

public interface InternalClientMethods {
    <T extends WarpRequirement> void registerRequirementRenderer(Class<T> clazz, RequirementRenderer<T> renderer);
}
