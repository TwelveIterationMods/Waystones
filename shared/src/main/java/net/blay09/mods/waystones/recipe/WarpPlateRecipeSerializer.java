package net.blay09.mods.waystones.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipeCodecs;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

class WarpPlateRecipeSerializer implements RecipeSerializer<WarpPlateRecipe> {

    private static final Codec<WarpPlateRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    CraftingRecipeCodecs.ITEMSTACK_OBJECT_CODEC.fieldOf("result").forGetter(w -> w.getResultItem()),
                    Ingredient.CODEC.fieldOf("inner").forGetter(WarpPlateRecipe::getInnerIngredient),
                    Ingredient.CODEC.listOf().fieldOf("outers")
                            .flatXmap(outers -> {
                                final int outerSize = outers.size();
                                if (outerSize != 4) {
                                    return DataResult.error(() -> "Bad WarpPlateRecipe, `outers` field must contain 4 ingredients but got " + outerSize);
                                }
                                if (outers.stream().anyMatch(Ingredient::isEmpty)) {
                                    return DataResult.error(() -> "Bad WarpPlateRecipe, `outers` ingredients must not be empty/air", outers);
                                }
                                return DataResult.success(outers);
                            }, DataResult::success)
                            .forGetter(WarpPlateRecipe::getOuterShapelessIngredients)
                    )
            .apply(instance, (itemStack, center, outers) -> new WarpPlateRecipe(itemStack.getItem(), itemStack.getCount(), center, outers)));

    @Override
    public Codec<WarpPlateRecipe> codec() {
        return CODEC;
    }

    @Override
    public WarpPlateRecipe fromNetwork(FriendlyByteBuf buf) {
        ItemStack stack = buf.readItem();
        Ingredient inner = Ingredient.fromNetwork(buf);
        boolean isSameOuter = buf.readBoolean();
        Ingredient firstOuter = Ingredient.fromNetwork(buf);
        NonNullList<Ingredient> outers;
        if (isSameOuter) {
            outers = NonNullList.withSize(4, firstOuter);
        }
        else {
            outers = NonNullList.createWithCapacity(4);
            outers.add(firstOuter);
            outers.add(Ingredient.fromNetwork(buf));
            outers.add(Ingredient.fromNetwork(buf));
            outers.add(Ingredient.fromNetwork(buf));
        }
        return new WarpPlateRecipe(stack.getItem(), stack.getCount(), inner, outers);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, WarpPlateRecipe recipe) {
        buf.writeItem(recipe.getResultItem());
        recipe.getInnerIngredient().toNetwork(buf);
        buf.writeBoolean(recipe.isSameOuterIngredients());
        if (recipe.isSameOuterIngredients()) {
            recipe.getOuterShapelessIngredients().get(0).toNetwork(buf);
        }
        else {
            recipe.getOuterShapelessIngredients().forEach(i -> i.toNetwork(buf));
        }
    }
}
