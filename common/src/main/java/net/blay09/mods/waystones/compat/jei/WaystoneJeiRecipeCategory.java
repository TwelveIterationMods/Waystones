package net.blay09.mods.waystones.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.recipe.WaystoneRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class WaystoneJeiRecipeCategory implements IRecipeCategory<WaystoneRecipe> {

    public static final RecipeType<WaystoneRecipe> TYPE = RecipeType.create(Waystones.MOD_ID, "waystone", WaystoneRecipe.class);
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(Waystones.MOD_ID, "waystone");
    private static final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(Waystones.MOD_ID, "textures/gui/jei/warp_plate.png");

    private final IDrawable background;
    private final IDrawable icon;

    public WaystoneJeiRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(texture, 0, 0, 128, 74);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.warpPlate));
    }

    @Override
    public RecipeType<WaystoneRecipe> getRecipeType() {
        return TYPE;
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
    public void setRecipe(IRecipeLayoutBuilder builder, WaystoneRecipe recipe, IFocusGroup focuses) {
        final var inputs = recipe.getIngredients();
        builder.addSlot(RecipeIngredientRole.INPUT, 29, 29)
                .addIngredients(inputs.get(0));

        builder.addSlot(RecipeIngredientRole.INPUT, 29, 1)
                .addIngredients(inputs.get(1));

        builder.addSlot(RecipeIngredientRole.INPUT, 57, 29)
                .addIngredients(inputs.get(2));

        builder.addSlot(RecipeIngredientRole.INPUT, 29, 57)
                .addIngredients(inputs.get(3));

        builder.addSlot(RecipeIngredientRole.INPUT, 1, 29)
                .addIngredients(inputs.get(4));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 111, 29)
                .addIngredient(VanillaTypes.ITEM_STACK, recipe.getOutputItem());
    }

}
