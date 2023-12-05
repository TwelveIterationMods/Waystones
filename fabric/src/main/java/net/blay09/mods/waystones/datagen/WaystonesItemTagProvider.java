package net.blay09.mods.waystones.datagen;

import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.item.ModItems;
import net.blay09.mods.waystones.tag.ModItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class WaystonesItemTagProvider extends FabricTagProvider<Item> {
    public WaystonesItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.ITEM, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        getOrCreateTagBuilder(ModItemTags.WARP_SCROLLS).add(ModItems.warpScroll);
        getOrCreateTagBuilder(ModItemTags.RETURN_SCROLLS).add(ModItems.returnScroll);
        getOrCreateTagBuilder(ModItemTags.BOUND_SCROLLS).add(ModItems.boundScroll);
        getOrCreateTagBuilder(ModItemTags.WARP_STONES).add(ModItems.warpStone);
        getOrCreateTagBuilder(ModItemTags.WARP_SHARDS).add(ModItems.attunedShard, ModItems.crumblingAttunedShard);
        getOrCreateTagBuilder(ModItemTags.SINGLE_USE_WARP_SHARDS).add(ModItems.crumblingAttunedShard);
        getOrCreateTagBuilder(ModItemTags.WAYSTONES).add(ModBlocks.waystone.asItem(), ModBlocks.mossyWaystone.asItem(), ModBlocks.sandyWaystone.asItem());
        FabricTagProvider<Item>.FabricTagBuilder sharestonesTag = getOrCreateTagBuilder(ModItemTags.SHARESTONES);
        sharestonesTag.add(ModBlocks.sharestone.asItem());
        FabricTagProvider<Item>.FabricTagBuilder dyedSharestonesTag = getOrCreateTagBuilder(ModItemTags.DYED_SHARESTONES);
        for (Block scopedSharestone : ModBlocks.scopedSharestones) {
            sharestonesTag.add(scopedSharestone.asItem());
            dyedSharestonesTag.add(scopedSharestone.asItem());
        }
    }
}
