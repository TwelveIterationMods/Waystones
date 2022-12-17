package net.blay09.mods.waystones.datagen;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class WaystonesBlockTagProvider extends FabricTagProvider<Block> {
    public WaystonesBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.BLOCK, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        TagKey<Block> mineablePickaxe = TagKey.create(Registries.BLOCK, new ResourceLocation("minecraft", "mineable/pickaxe"));
        FabricTagProvider<Block>.FabricTagBuilder builder = getOrCreateTagBuilder(mineablePickaxe);
        builder.add(ModBlocks.waystone, ModBlocks.sandyWaystone, ModBlocks.mossyWaystone, ModBlocks.warpPlate, ModBlocks.portstone, ModBlocks.sharestone);
        for (Block scopedSharestone : ModBlocks.scopedSharestones) {
            builder.add(scopedSharestone);
        }
    }
}
