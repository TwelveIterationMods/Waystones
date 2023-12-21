package net.blay09.mods.waystones.datagen;

import net.blay09.mods.balm.api.tag.BalmItemTags;
import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.item.ModItems;
import net.blay09.mods.waystones.tag.ModItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import static net.minecraft.data.recipes.ShapedRecipeBuilder.shaped;
import static net.minecraft.data.recipes.ShapelessRecipeBuilder.shapeless;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void buildRecipes(RecipeOutput exporter) {
        shaped(RecipeCategory.DECORATIONS, ModBlocks.waystone)
                .pattern(" S ")
                .pattern("SWS")
                .pattern("OOO")
                .define('S', Blocks.STONE_BRICKS)
                .define('W', ModItems.warpStone)
                .define('O', Blocks.OBSIDIAN)
                .unlockedBy("has_warp_stone", has(ModItems.warpStone))
                .save(exporter);

        shaped(RecipeCategory.DECORATIONS, ModBlocks.sandyWaystone)
                .pattern(" S ")
                .pattern("SWS")
                .pattern("OOO")
                .define('S', Blocks.CHISELED_SANDSTONE)
                .define('W', ModItems.warpStone)
                .define('O', Blocks.OBSIDIAN)
                .unlockedBy("has_warp_stone", has(ModItems.warpStone))
                .save(exporter);

        shaped(RecipeCategory.DECORATIONS, ModBlocks.mossyWaystone)
                .pattern(" S ")
                .pattern("SWS")
                .pattern("OOO")
                .define('S', Blocks.MOSSY_STONE_BRICKS)
                .define('W', ModItems.warpStone)
                .define('O', Blocks.OBSIDIAN)
                .unlockedBy("has_warp_stone", has(ModItems.warpStone))
                .save(exporter);

        shaped(RecipeCategory.DECORATIONS, ModBlocks.deepslateWaystone)
                .pattern(" S ")
                .pattern("SWS")
                .pattern("OOO")
                .define('S', Blocks.DEEPSLATE)
                .define('W', ModItems.warpStone)
                .define('O', Blocks.OBSIDIAN)
                .unlockedBy("has_warp_stone", has(ModItems.warpStone))
                .save(exporter);

        shaped(RecipeCategory.DECORATIONS, ModBlocks.blackstoneWaystone)
                .pattern(" S ")
                .pattern("SWS")
                .pattern("OOO")
                .define('S', Blocks.BLACKSTONE)
                .define('W', ModItems.warpStone)
                .define('O', Blocks.OBSIDIAN)
                .unlockedBy("has_warp_stone", has(ModItems.warpStone))
                .save(exporter);

        shaped(RecipeCategory.DECORATIONS, ModBlocks.endStoneWaystone)
                .pattern(" S ")
                .pattern("SWS")
                .pattern("OOO")
                .define('S', Blocks.END_STONE_BRICKS)
                .define('W', ModItems.warpStone)
                .define('O', Blocks.OBSIDIAN)
                .unlockedBy("has_warp_stone", has(ModItems.warpStone))
                .save(exporter);

        shapeless(RecipeCategory.DECORATIONS, ModBlocks.mossyWaystone)
                .requires(ModBlocks.waystone)
                .requires(Blocks.VINE, 3)
                .unlockedBy("has_waystone", has(ModBlocks.waystone))
                .save(exporter, new ResourceLocation("waystones", "mossy_waystone_from_vines"));

        shaped(RecipeCategory.DECORATIONS, ModBlocks.portstone)
                .pattern(" S ")
                .pattern("SWS")
                .pattern("BBB")
                .define('S', Blocks.STONE_BRICKS)
                .define('W', ModItems.warpStone)
                .define('B', Blocks.POLISHED_ANDESITE)
                .unlockedBy("has_warp_stone", has(ModItems.warpStone))
                .save(exporter);

        shaped(RecipeCategory.DECORATIONS, ModBlocks.warpPlate)
                .pattern("SWS")
                .pattern("WFW")
                .pattern("SWS")
                .define('F', Items.FLINT)
                .define('W', ModItems.warpDust)
                .define('S', Blocks.STONE_BRICKS)
                .unlockedBy("has_warp_dust", has(ModItems.warpDust))
                .save(exporter);

        shaped(RecipeCategory.DECORATIONS, ModBlocks.landingStone)
                .pattern(" S ")
                .pattern("WFW")
                .pattern(" S ")
                .define('F', Items.FLINT)
                .define('W', ModItems.warpDust)
                .define('S', Blocks.STONE_BRICKS)
                .unlockedBy("has_warp_dust", has(ModItems.warpDust))
                .save(exporter);

        shaped(RecipeCategory.DECORATIONS, ModBlocks.sharestone)
                .pattern("SSS")
                .pattern(" W ")
                .pattern("OOO")
                .define('S', Blocks.STONE_BRICKS)
                .define('W', ModItems.warpStone)
                .define('O', Blocks.OBSIDIAN)
                .unlockedBy("has_warp_stone", has(ModItems.warpStone))
                .save(exporter);

        shapeless(RecipeCategory.DECORATIONS, ModBlocks.sharestone)
                .requires(Items.BONE_MEAL)
                .requires(ModItemTags.DYED_SHARESTONES)
                .unlockedBy("has_sharestone", has(ModItemTags.DYED_SHARESTONES))
                .save(exporter, new ResourceLocation("waystones", "sharestone_from_dyed"));

        createScopedSharestoneRecipe(exporter, DyeColor.WHITE, BalmItemTags.WHITE_DYES);
        createScopedSharestoneRecipe(exporter, DyeColor.ORANGE, BalmItemTags.ORANGE_DYES);
        createScopedSharestoneRecipe(exporter, DyeColor.MAGENTA, BalmItemTags.MAGENTA_DYES);
        createScopedSharestoneRecipe(exporter, DyeColor.LIGHT_BLUE, BalmItemTags.LIGHT_BLUE_DYES);
        createScopedSharestoneRecipe(exporter, DyeColor.YELLOW, BalmItemTags.YELLOW_DYES);
        createScopedSharestoneRecipe(exporter, DyeColor.LIME, BalmItemTags.LIME_DYES);
        createScopedSharestoneRecipe(exporter, DyeColor.PINK, BalmItemTags.PINK_DYES);
        createScopedSharestoneRecipe(exporter, DyeColor.GRAY, BalmItemTags.GRAY_DYES);
        createScopedSharestoneRecipe(exporter, DyeColor.LIGHT_GRAY, BalmItemTags.LIGHT_GRAY_DYES);
        createScopedSharestoneRecipe(exporter, DyeColor.CYAN, BalmItemTags.CYAN_DYES);
        createScopedSharestoneRecipe(exporter, DyeColor.PURPLE, BalmItemTags.PURPLE_DYES);
        createScopedSharestoneRecipe(exporter, DyeColor.BLUE, BalmItemTags.BLUE_DYES);
        createScopedSharestoneRecipe(exporter, DyeColor.BROWN, BalmItemTags.BROWN_DYES);
        createScopedSharestoneRecipe(exporter, DyeColor.GREEN, BalmItemTags.GREEN_DYES);
        createScopedSharestoneRecipe(exporter, DyeColor.RED, BalmItemTags.RED_DYES);
        createScopedSharestoneRecipe(exporter, DyeColor.BLACK, BalmItemTags.BLACK_DYES);

        shaped(RecipeCategory.DECORATIONS, ModItems.warpStone)
                .pattern("DED")
                .pattern("EGE")
                .pattern("DED")
                .define('G', BalmItemTags.EMERALDS)
                .define('E', Items.ENDER_PEARL)
                .define('D', Items.AMETHYST_SHARD)
                .unlockedBy("has_ender_pearl", has(Items.ENDER_PEARL))
                .save(exporter);

        shaped(RecipeCategory.DECORATIONS, ModItems.warpScroll, 3)
                .pattern("GDG")
                .pattern("GEG")
                .pattern("PPP")
                .define('D', BalmItemTags.PURPLE_DYES)
                .define('G', BalmItemTags.GOLD_NUGGETS)
                .define('E', Items.ENDER_PEARL)
                .define('P', Items.PAPER)
                .unlockedBy("has_ender_pearl", has(Items.ENDER_PEARL))
                .save(exporter);

        shaped(RecipeCategory.DECORATIONS, ModItems.returnScroll, 3)
                .pattern("GEG")
                .pattern("PPP")
                .define('E', BalmItemTags.PURPLE_DYES)
                .define('G', BalmItemTags.GOLD_NUGGETS)
                .define('P', Items.PAPER)
                .unlockedBy("has_paper", has(Items.PAPER))
                .save(exporter);

        shapeless(RecipeCategory.DECORATIONS, ModItems.warpDust, 4)
                .requires(Items.ENDER_PEARL)
                .requires(Items.AMETHYST_SHARD)
                .unlockedBy("has_ender_pearl", has(Items.ENDER_PEARL))
                .save(exporter);
    }

    private static void createScopedSharestoneRecipe(RecipeOutput exporter, DyeColor color, TagKey<Item> dyeTag) {
        shapeless(RecipeCategory.DECORATIONS, ModBlocks.scopedSharestones[color.ordinal()])
                .requires(ModItemTags.SHARESTONES)
                .requires(dyeTag)
                .unlockedBy("has_sharestone", has(ModItemTags.SHARESTONES))
                .save(exporter);
    }
}
