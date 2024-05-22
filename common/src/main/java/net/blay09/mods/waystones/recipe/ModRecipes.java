package net.blay09.mods.waystones.recipe;

import net.blay09.mods.balm.api.recipe.BalmRecipes;
import net.blay09.mods.waystones.Waystones;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class ModRecipes {

    public static final String WAYSTONE_RECIPE_GROUP = "waystone";
    public static final ResourceLocation WAYSTONE_RECIPE_TYPE = new ResourceLocation(Waystones.MOD_ID, WAYSTONE_RECIPE_GROUP);

    public static RecipeType<WaystoneRecipe> waystoneRecipeType;
    public static RecipeSerializer<WaystoneRecipe> waystoneRecipeSerializer;

    public static void initialize(BalmRecipes registry) {
        registry.registerRecipeType(() -> waystoneRecipeType = new RecipeType<>() {
                    @Override
                    public String toString() {
                        return WAYSTONE_RECIPE_GROUP;
                    }
                },
                () -> waystoneRecipeSerializer = new WaystoneRecipe.Serializer(), WAYSTONE_RECIPE_TYPE);
    }
}
