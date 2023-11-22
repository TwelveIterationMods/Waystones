package net.blay09.mods.waystones.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipeCodecs;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.Optional;

class WarpPlateRecipeSerializer implements RecipeSerializer<WarpPlateRecipe> {

    private static final Codec<WarpPlateRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    CraftingRecipeCodecs.ITEMSTACK_OBJECT_CODEC.fieldOf("result").forGetter(w -> w.getResultItem()),
                    Ingredient.CODEC.fieldOf("inner").forGetter(WarpPlateRecipe::getInnerIngredient),
                    Ingredient.CODEC.optionalFieldOf("outer").forGetter((WarpPlateRecipe w) -> {
                        if (w.isSameOuterIngredients()) {
                            return Optional.of(w.getOuterShapelessIngredients().get(0));
                        }
                        return Optional.empty();
                    }),
                    Codec.optionalField("outers", Codec.list(Ingredient.CODEC)).forGetter(w -> {
                        if (w.isSameOuterIngredients()) {
                            return Optional.empty();
                        }
                        return Optional.of(w.getOuterShapelessIngredients());
                    }))
            .apply(instance, (itemStack, center, oOuter, oListOuters) -> {
                if (oOuter.isPresent() == oListOuters.isPresent()) {
                    throw new IllegalStateException("Bad WarpPlateRecipe, exactly one of 'outer' (single ingredient) or 'outers' (4 ingredients array) must be present");
                }
                if (oListOuters.isPresent()) {
                    return new WarpPlateRecipe(itemStack.getItem(), itemStack.getCount(), center, oListOuters.get());
                }
                return new WarpPlateRecipe(itemStack.getItem(), itemStack.getCount(), center, oOuter.get());
            })
    );

    @Override
    public Codec<WarpPlateRecipe> codec() {
        return CODEC;
    }

    @Override
    public WarpPlateRecipe fromNetwork(FriendlyByteBuf buf) {
        ItemStack stack = buf.readItem();
        Ingredient inner = Ingredient.fromNetwork(buf);
        boolean isSameOuter = buf.readBoolean();
        if (isSameOuter) {
            Ingredient outer = Ingredient.fromNetwork(buf);
            return new WarpPlateRecipe(stack.getItem(), stack.getCount(), inner, outer);
        }
        else {
            NonNullList<Ingredient> outers = NonNullList.createWithCapacity(4);
            outers.add(Ingredient.fromNetwork(buf));
            outers.add(Ingredient.fromNetwork(buf));
            outers.add(Ingredient.fromNetwork(buf));
            outers.add(Ingredient.fromNetwork(buf));
            return new WarpPlateRecipe(stack.getItem(), stack.getCount(), inner, outers);
        }
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
