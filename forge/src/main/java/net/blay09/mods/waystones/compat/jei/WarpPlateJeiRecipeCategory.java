package net.blay09.mods.waystones.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.ModBlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class WarpPlateJeiRecipeCategory implements IRecipeCategory<AttunedShardJeiRecipe> {

    private static final ResourceLocation texture = new ResourceLocation(Waystones.MOD_ID, "textures/gui/jei/warp_plate.png");
    public static final ResourceLocation UID = new ResourceLocation(Waystones.MOD_ID, "warp_plate");

    private final IDrawable background;
    private final IDrawable icon;

    public WarpPlateJeiRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(texture, 0, 0, 128, 74);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(ModBlocks.warpPlate));
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends AttunedShardJeiRecipe> getRecipeClass() {
        return AttunedShardJeiRecipe.class;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(UID.toString());
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setIngredients(AttunedShardJeiRecipe recipe, IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM, recipe.getInputs());
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getOutput());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, final AttunedShardJeiRecipe recipe, IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, true, 28, 28);
        recipeLayout.getItemStacks().set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));

        recipeLayout.getItemStacks().init(1, true, 28, 0);
        recipeLayout.getItemStacks().set(1, ingredients.getInputs(VanillaTypes.ITEM).get(1));

        recipeLayout.getItemStacks().init(2, true, 56, 28);
        recipeLayout.getItemStacks().set(2, ingredients.getInputs(VanillaTypes.ITEM).get(2));

        recipeLayout.getItemStacks().init(3, true, 28, 56);
        recipeLayout.getItemStacks().set(3, ingredients.getInputs(VanillaTypes.ITEM).get(3));

        recipeLayout.getItemStacks().init(4, true, 0, 28);
        recipeLayout.getItemStacks().set(4, ingredients.getInputs(VanillaTypes.ITEM).get(4));

        recipeLayout.getItemStacks().init(5, false, 110, 28);
        recipeLayout.getItemStacks().set(5, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
    }
}
