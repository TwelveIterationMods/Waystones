package net.blay09.mods.waystones.datagen;

import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.tag.ModBlockTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider<Block> {
    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.BLOCK, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        TagKey<Block> mineablePickaxeTag = TagKey.create(Registries.BLOCK, new ResourceLocation("minecraft", "mineable/pickaxe"));
        FabricTagProvider<Block>.FabricTagBuilder mineableBuilder = getOrCreateTagBuilder(mineablePickaxeTag);
        mineableBuilder.add(ModBlocks.waystone,
                ModBlocks.sandyWaystone,
                ModBlocks.mossyWaystone,
                ModBlocks.deepslateWaystone,
                ModBlocks.blackstoneWaystone,
                ModBlocks.endStoneWaystone,
                ModBlocks.warpPlate,
                ModBlocks.portstone,
                ModBlocks.landingStone);
        for (Block sharestone : ModBlocks.sharestones) {
            mineableBuilder.add(sharestone);
        }

        FabricTagProvider<Block>.FabricTagBuilder isTeleportTargetBuilder = getOrCreateTagBuilder(ModBlockTags.IS_TELEPORT_TARGET);
        isTeleportTargetBuilder.add(ModBlocks.waystone,
                ModBlocks.sandyWaystone,
                ModBlocks.mossyWaystone,
                ModBlocks.deepslateWaystone,
                ModBlocks.blackstoneWaystone,
                ModBlocks.endStoneWaystone,
                ModBlocks.warpPlate,
                ModBlocks.portstone,
                ModBlocks.landingStone);
        for (Block sharestone : ModBlocks.sharestones) {
            isTeleportTargetBuilder.add(sharestone);
        }

        getOrCreateTagBuilder(ModBlockTags.WAYSTONES).add(ModBlocks.waystone,
                ModBlocks.sandyWaystone,
                ModBlocks.mossyWaystone,
                ModBlocks.deepslateWaystone,
                ModBlocks.blackstoneWaystone,
                ModBlocks.endStoneWaystone);

        FabricTagProvider<Block>.FabricTagBuilder sharestonesBuilder = getOrCreateTagBuilder(ModBlockTags.SHARESTONES);
        for (Block sharestone : ModBlocks.sharestones) {
            sharestonesBuilder.add(sharestone);
        }
    }

}
