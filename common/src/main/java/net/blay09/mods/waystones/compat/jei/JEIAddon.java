package net.blay09.mods.waystones.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.recipe.ModRecipes;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

@JeiPlugin
public class JEIAddon implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(Waystones.MOD_ID, "jei");
    }

    @Override
    public void registerRecipes(IRecipeRegistration registry) {
        final var level = Minecraft.getInstance().level;
        final var recipeManager = level.getRecipeManager();
        registry.addRecipes(WaystoneJeiRecipeCategory.TYPE,
                recipeManager.getAllRecipesFor(ModRecipes.waystoneRecipeType).stream().map(RecipeHolder::value).toList());
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(new WaystoneJeiRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
    }
}
