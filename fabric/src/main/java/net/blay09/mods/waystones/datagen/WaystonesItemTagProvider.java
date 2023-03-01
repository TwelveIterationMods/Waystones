package net.blay09.mods.waystones.datagen;

import net.blay09.mods.waystones.item.ModItems;
import net.blay09.mods.waystones.tag.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

import java.util.concurrent.CompletableFuture;

public class WaystonesItemTagProvider extends FabricTagProvider<Item> {
    public WaystonesItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.ITEM, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        getOrCreateTagBuilder(ModTags.WARP_SCROLLS).add(ModItems.warpScroll);
        getOrCreateTagBuilder(ModTags.RETURN_SCROLLS).add(ModItems.returnScroll);
        getOrCreateTagBuilder(ModTags.BOUND_SCROLLS).add(ModItems.boundScroll);
        getOrCreateTagBuilder(ModTags.WARP_STONES).add(ModItems.warpStone);
    }
}
