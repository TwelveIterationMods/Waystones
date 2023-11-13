package net.blay09.mods.waystones.recipe;

import net.blay09.mods.waystones.Waystones;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class ModRecipes {

    public static final String WARP_PLATE_RECIPE_GROUP = "warp_plate";
    public static final ResourceLocation WARP_PLATE_RECIPE_TYPE = new ResourceLocation(Waystones.MOD_ID, WARP_PLATE_RECIPE_GROUP);

    public static RecipeType<WarpPlateRecipe> warpPlateRecipeType;

    public static void initialize() {
        warpPlateRecipeType = RecipeType.register(WARP_PLATE_RECIPE_TYPE.toString());
        RecipeSerializer.register(WARP_PLATE_RECIPE_TYPE.toString(), new WarpPlateRecipeSerializer());
    }
}
