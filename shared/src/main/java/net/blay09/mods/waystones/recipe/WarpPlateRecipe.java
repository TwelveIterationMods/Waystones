package net.blay09.mods.waystones.recipe;

import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.block.entity.WarpPlateBlockEntity;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public class WarpPlateRecipe implements Recipe<WarpPlateBlockEntity> {
    private final Ingredient innerIngredient;
    private final NonNullList<Ingredient> outerIngredients;
    private final boolean isSameOuterIngredients;
    private final ItemStack result;

    private final NonNullList<Ingredient> ingredients;
    private String str;

    public WarpPlateRecipe(Item resultItem, int resultCount, Ingredient innerIngredient, List<Ingredient> outerIngredients) {
        this.result = new ItemStack(resultItem, resultCount);
        this.innerIngredient = innerIngredient;
        if (outerIngredients.size() != 4) {
            throw new IllegalArgumentException("Bad WarpPlateRecipe outerIngredients count, expected 4 but got " + outerIngredients.size());
        }
        this.outerIngredients = NonNullList.createWithCapacity(4);
        this.outerIngredients.addAll(outerIngredients);
        this.ingredients = NonNullList.createWithCapacity(5);
        this.ingredients.add(innerIngredient);
        this.ingredients.addAll(outerIngredients);
        this.isSameOuterIngredients = outerIngredients.stream().distinct().count() == 1;
    }

    public static Optional<WarpPlateRecipe> findFirstMatchingRecipe(WarpPlateBlockEntity inventory) {
        return inventory.getLevel().getRecipeManager().getRecipeFor(
                        ModRecipes.warpPlateRecipeType,
                        inventory, inventory.getLevel())
                .map(RecipeHolder::value);
    }

    @Override
    public boolean matches(WarpPlateBlockEntity inventory, Level level) {
        if (inventory.getItems().stream().anyMatch(ItemStack::isEmpty)) return false;
        if (!innerIngredient.test(inventory.getItem(0))) return false;
        //optimize the case where outerIngredients are all the same
        if (this.isSameOuterIngredients()) {
            Ingredient outerIngredient = this.outerIngredients.get(0);
            return outerIngredient.test(inventory.getItem(1)) &&
                    outerIngredient.test(inventory.getItem(2)) &&
                    outerIngredient.test(inventory.getItem(3)) &&
                    outerIngredient.test(inventory.getItem(4));
        }
        StackedContents stackedContents = new StackedContents();
        int i = 0;
        for (int j = 0; j < 5; j++) {
            ItemStack itemStack = inventory.getItem(j);
            if (itemStack.isEmpty()) continue;
            i++;
            stackedContents.accountStack(itemStack, 1);
        }
        return i == 5 && stackedContents.canCraft(this, null);
    }

    /**
     * The {@link Ingredient} to accept in the central slot (slot 0).
     */
    public Ingredient getInnerIngredient() {
        return innerIngredient;
    }

    /**
     * The {@link Ingredient}s to accept in the 4 outer slots, shapeless (1 top, 2 right, 3 bottom and 4 left).
     */
    public NonNullList<Ingredient> getOuterShapelessIngredients() {
        return this.outerIngredients;
    }

    /**
     * Are the outer Ingredients all the same, ie was the recipe declared only with a single `outerIngredient`
     * rather than an array of `outerIngredients`?
     */
    public boolean isSameOuterIngredients() {
        return this.isSameOuterIngredients;
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
        if (this.str == null) {
            StringBuilder strb = new StringBuilder("WarpPlateRecipe{");
            strb.append(getResultItem())
                    .append(", inner=")
                    .append(getInnerIngredient().toJson(false))
                    .append(", outers=");
            if (isSameOuterIngredients) {
                strb.append("4x")
                        .append(getOuterShapelessIngredients().get(0).toJson(false));
            }
            else {
                strb.append("[");
                getOuterShapelessIngredients().forEach(i -> strb.append(i.toJson(false)));
                strb.append("]");
            }
            this.str = strb.toString();
        }
        return this.str;
    }

    @Override
    public boolean isIncomplete() {
        return Recipe.super.isIncomplete();
    }
}
