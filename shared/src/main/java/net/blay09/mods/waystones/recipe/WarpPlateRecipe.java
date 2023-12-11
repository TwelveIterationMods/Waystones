package net.blay09.mods.waystones.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.waystones.block.ModBlocks;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class WarpPlateRecipe implements Recipe<Container> {

    private final ItemStack resultItem;
    private final Ingredient primaryIngredient;
    private final NonNullList<Ingredient> secondaryIngredients;
    private final NonNullList<Ingredient> combinedIngredients;

    public WarpPlateRecipe(ItemStack resultItem, Ingredient primaryIngredient, NonNullList<Ingredient> secondaryIngredients) {
        this.resultItem = resultItem;

        this.primaryIngredient = primaryIngredient;
        this.secondaryIngredients = secondaryIngredients;

        this.combinedIngredients = NonNullList.createWithCapacity(5);
        this.combinedIngredients.add(primaryIngredient);
        this.combinedIngredients.addAll(secondaryIngredients);
    }

    @Override
    public boolean matches(Container inventory, Level level) {
        // Short-circuit if the primary ingredient is not present to ensure it's actually in the right slot and avoid unnecessary processing
        if (!primaryIngredient.test(inventory.getItem(0))) {
            return false;
        }

        StackedContents stackedContents = new StackedContents();
        int foundInputs = 0;
        // canCraft uses getIngredients, so we need to include the primary ingredient here as well
        for (int i = 0; i < combinedIngredients.size(); i++) {
            ItemStack itemStack = inventory.getItem(i);
            if (!itemStack.isEmpty()) {
                foundInputs++;
                stackedContents.accountStack(itemStack, 1);
            }
        }
        return foundInputs == combinedIngredients.size() && stackedContents.canCraft(this, null);
    }

    @Override
    public ItemStack assemble(Container inventory, RegistryAccess registryAccess) {
        return this.resultItem.copy();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return this.resultItem;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.combinedIngredients;
    }

    @Override
    public boolean isSpecial() {
        return true;
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
        return ModRecipes.warpPlateRecipeSerializer;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.warpPlateRecipeType;
    }

    static class Serializer implements RecipeSerializer<WarpPlateRecipe> {

        private static final Codec<WarpPlateRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        CraftingRecipeCodecs.ITEMSTACK_OBJECT_CODEC.fieldOf("result").forGetter(recipe -> recipe.resultItem),
                        Ingredient.CODEC.fieldOf("primary").forGetter(recipe -> recipe.primaryIngredient),
                        Ingredient.CODEC.listOf().fieldOf("secondary")
                                .flatXmap(secondary -> {
                                    Ingredient[] ingredients = secondary.stream().filter((ingredient) -> !ingredient.isEmpty()).toArray(Ingredient[]::new);
                                    if (ingredients.length == 0) {
                                        return DataResult.error(() -> "No secondary ingredients for warp plate recipe");
                                    } else {
                                        return ingredients.length > 4 ? DataResult.error(() -> "Too many secondary ingredients for warp plate recipe") : DataResult.success(
                                                NonNullList.of(Ingredient.EMPTY, ingredients));
                                    }
                                }, DataResult::success)
                                .forGetter(recipe -> recipe.secondaryIngredients)
                )
                .apply(instance, WarpPlateRecipe::new));

        @Override
        public Codec<WarpPlateRecipe> codec() {
            return CODEC;
        }

        @Override
        public WarpPlateRecipe fromNetwork(FriendlyByteBuf buf) {
            final var resultItem = buf.readItem();
            final var primaryIngredient = Ingredient.fromNetwork(buf);
            final var secondaryCount = buf.readVarInt();
            final NonNullList<Ingredient> secondaryIngredients = NonNullList.createWithCapacity(secondaryCount);
            for (int i = 0; i < secondaryCount; i++) {
                secondaryIngredients.add(Ingredient.fromNetwork(buf));
            }
            return new WarpPlateRecipe(resultItem, primaryIngredient, secondaryIngredients);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, WarpPlateRecipe recipe) {
            buf.writeItem(recipe.resultItem);
            recipe.primaryIngredient.toNetwork(buf);
            buf.writeVarInt(recipe.secondaryIngredients.size());
            for (Ingredient ingredient : recipe.secondaryIngredients) {
                ingredient.toNetwork(buf);
            }
        }
    }

}
