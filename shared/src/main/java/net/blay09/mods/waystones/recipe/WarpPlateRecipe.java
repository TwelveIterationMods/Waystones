package net.blay09.mods.waystones.recipe;

import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.block.entity.WarpPlateBlockEntity;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.stream.Stream;

public class WarpPlateRecipe implements Recipe<WarpPlateBlockEntity> {
    private final Ingredient innerIngredient;
    private final Ingredient outerIngredient;
    private final ItemStack result;

    private final NonNullList<Ingredient> ingredients;

    public WarpPlateRecipe(Item resultItem, int resultCount, Ingredient innerIngredient, Ingredient outerIngredient) {
        this.result = new ItemStack(resultItem, resultCount);
        this.innerIngredient = innerIngredient;
        this.outerIngredient = outerIngredient;
        this.ingredients = NonNullList.of(this.innerIngredient, this.outerIngredient, this.outerIngredient, this.outerIngredient, this.outerIngredient);
    }

    public static Stream<WarpPlateRecipe> findAllWarpPlateRecipes(Level level) {
        //TODO would it benefit to add some caching ?
        return level.getRecipeManager().getAllRecipesFor(ModRecipes.warpPlateRecipeType).stream().map(RecipeHolder::value);
    }

    @Override
    public boolean matches(WarpPlateBlockEntity inventory, Level level) {
        if (inventory.getItems().size() < 5) return false;
        if (!innerIngredient.test(inventory.getItem(0))) return false;
        return outerIngredient.test(inventory.getItem(1)) &&
                outerIngredient.test(inventory.getItem(2)) &&
                outerIngredient.test(inventory.getItem(3)) &&
                outerIngredient.test(inventory.getItem(4));
    }

    /**
     * The {@link Ingredient} to accept in the central slot (slot 0).
     */
    public Ingredient getInnerIngredient() {
        return innerIngredient;
    }

    /**
     * The {@link Ingredient} to accept in the 4 outer slots (1 top, 2 right, 3 bottom and 4 left).
     */
    public Ingredient getOuterIngredient() {
        return this.outerIngredient;
    }

    @Override
    public ItemStack assemble(WarpPlateBlockEntity inventory, RegistryAccess registryAccess) {
        return this.result.copy();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return this.result;
    }

    public ItemStack getResultItem() {
        return getResultItem(RegistryAccess.EMPTY);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return Recipe.super.showNotification();
    }

    @Override
    public String getGroup() {
        return ModRecipes.WARP_PLATE_RECIPE_GROUP;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.warpPlate);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return new WarpPlateRecipeSerializer();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.warpPlateRecipeType;
    }


    @Override
    public String toString() {
        return "WarpPlateRecipe{" + getResultItem() + ", inner=" + getInnerIngredient().toJson(false) + ", outer=" + getOuterIngredient().toJson(false) + "}";
    }

    @Override
    public boolean isIncomplete() {
        return Recipe.super.isIncomplete();
    }
}
