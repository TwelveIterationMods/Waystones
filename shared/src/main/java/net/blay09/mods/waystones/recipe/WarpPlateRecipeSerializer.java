package net.blay09.mods.waystones.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipeCodecs;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

class WarpPlateRecipeSerializer implements RecipeSerializer<WarpPlateRecipe> {

    private static final Codec<WarpPlateRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    CraftingRecipeCodecs.ITEMSTACK_OBJECT_CODEC.fieldOf("result").forGetter(WarpPlateRecipe::getResultItem),
                    Ingredient.CODEC.fieldOf("inner").forGetter(WarpPlateRecipe::getInnerIngredient),
                    Ingredient.CODEC.fieldOf("outer").forGetter(WarpPlateRecipe::getOuterIngredient))
            //note that the count from the recipe is capped to the item's maxStackSize (see WarpPlateRecipe constructor)
            .apply(instance, (itemStack, c, o) -> new WarpPlateRecipe(itemStack.getItem(), itemStack.getCount(), c, o))
    );

    @Override
    public Codec<WarpPlateRecipe> codec() {
        return CODEC;
    }

    @Override
    public WarpPlateRecipe fromNetwork(FriendlyByteBuf buf) {
        ItemStack stack = buf.readItem();
        Ingredient inner = Ingredient.fromNetwork(buf);
        Ingredient outer = Ingredient.fromNetwork(buf);

        return new WarpPlateRecipe(stack.getItem(), stack.getCount(), inner, outer);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, WarpPlateRecipe recipe) {
        buf.writeItem(recipe.getResultItem());
        recipe.getInnerIngredient().toNetwork(buf);
        recipe.getOuterIngredient().toNetwork(buf);
    }
}
