package net.blay09.mods.waystones.recipe;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class WaystoneRecipeBuilder implements RecipeBuilder {

    private Ingredient primaryIngredient = Ingredient.EMPTY;
    private NonNullList<Ingredient> secondaryIngredients = NonNullList.withSize(4, Ingredient.EMPTY);
    private final RecipeCategory category;
    private final ItemStack resultItem;
    private final Map<String, Criterion<?>> criteria = new HashMap<>();

    private WaystoneRecipeBuilder(RecipeCategory category, ItemStack resultItem) {
        this.category = category;
        this.resultItem = resultItem;
    }

    public static WaystoneRecipeBuilder waystone(RecipeCategory category, ItemStack resultItem) {
        return new WaystoneRecipeBuilder(category, resultItem);
    }

    public WaystoneRecipeBuilder primaryIngredient(Ingredient ingredient) {
        this.primaryIngredient = ingredient;
        return this;
    }

    public WaystoneRecipeBuilder addSecondaryIngredient(Ingredient ingredient) {
        this.secondaryIngredients.add(ingredient);
        return this;
    }

    public WaystoneRecipeBuilder setSecondaryIngredients(NonNullList<Ingredient> secondaryIngredients) {
        this.secondaryIngredients = secondaryIngredients;
        return this;
    }

    @Override
    public WaystoneRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        criteria.put(name, criterion);
        return this;
    }

    @Override
    public WaystoneRecipeBuilder group(@Nullable String groupName) {
        // unused
        return this;
    }

    @Override
    public Item getResult() {
        return resultItem.getItem();
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation resourceLocation) {
        validate();
        Advancement.Builder advancementBuilder = recipeOutput.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceLocation)).rewards(AdvancementRewards.Builder.recipe(resourceLocation)).requirements(AdvancementRequirements.Strategy.OR);
        Map<String, Criterion<?>> criterionMap = criteria;
        criterionMap.forEach(advancementBuilder::addCriterion);
        WaystoneRecipe recipe = new WaystoneRecipe(resultItem, primaryIngredient, secondaryIngredients);
        recipeOutput.accept(resourceLocation, recipe, advancementBuilder.build(resourceLocation.withPrefix("recipes/" + category.getFolderName() + "/")));
    }

    private void validate() {
        Objects.requireNonNull(primaryIngredient);
        Objects.requireNonNull(secondaryIngredients);
        Objects.requireNonNull(category);
        if (resultItem.isEmpty()) {
            throw new IllegalStateException("Result item must not be empty");
        }
        if (primaryIngredient.isEmpty()) {
            throw new IllegalStateException("Primary ingredient must not be empty");
        }
        if (secondaryIngredients.size() > 4) {
            throw new IllegalStateException("Secondary ingredients must not exceed 4");
        }
    }
}
