package net.blay09.mods.waystones.compat;

import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.config.WaystonesConfigData;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class RepurposedStructuresIntegration {
    public static final DeferredRegister<Supplier<Boolean>> RS_CONDITIONS_REGISTRY = DeferredRegister.create(
            new ResourceLocation("repurposed_structures", "json_conditions"), "waystones");

    public static final DeferredHolder<Supplier<Boolean>, Supplier<Boolean>> WAYSTONE_CONFIG_CONDITION = RS_CONDITIONS_REGISTRY.register(
            "config", () -> () -> WaystonesConfig.getActive().worldGen.spawnInVillages != WaystonesConfigData.VillageWaystoneGeneration.DISABLED);

    public RepurposedStructuresIntegration() {
        final var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        RS_CONDITIONS_REGISTRY.register(modEventBus);
    }
}
