package net.blay09.mods.waystones.datagen;

import net.blay09.mods.waystones.tag.ModBiomeTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

import java.util.concurrent.CompletableFuture;

public class ModBiomeTagProvider extends FabricTagProvider<Biome> {
    public ModBiomeTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.BIOME, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        getOrCreateTagBuilder(ModBiomeTags.IS_SWAMP).add(Biomes.SWAMP, Biomes.MANGROVE_SWAMP);
        getOrCreateTagBuilder(ModBiomeTags.IS_DESERT).add(Biomes.DESERT);
        getOrCreateTagBuilder(ModBiomeTags.IS_MUSHROOM).add(Biomes.MUSHROOM_FIELDS);
    }

}
