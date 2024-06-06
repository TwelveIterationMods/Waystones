package net.blay09.mods.waystones.requirement;

import net.blay09.mods.waystones.api.WaystoneCooldowns;
import net.blay09.mods.waystones.api.requirement.RequirementType;
import net.minecraft.resources.ResourceLocation;

public class CooldownRequirementType implements RequirementType<CooldownRequirement> {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("waystones", "cooldown");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public CooldownRequirement createInstance() {
        return new CooldownRequirement(WaystoneCooldowns.INVENTORY_BUTTON, 0);
    }
}
