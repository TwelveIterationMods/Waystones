//package net.blay09.mods.waystones.compat.jei;
//
//import com.google.common.collect.Lists;
//import mezz.jei.api.IModPlugin;
//import mezz.jei.api.JeiPlugin;
//import mezz.jei.api.registration.IRecipeCategoryRegistration;
//import mezz.jei.api.registration.IRecipeRegistration;
//import net.blay09.mods.waystones.Waystones;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.util.ResourceLocation;
//
//@JeiPlugin
//public class JEIAddon implements IModPlugin {
//    @Override
//    public ResourceLocation getPluginUid() {
//        return new ResourceLocation(WaystonesMod.ID, "jei");
//    }
//
//    @Override
//    public void registerRecipes(IRecipeRegistration registry) {
//        registry.addRecipes(Lists.newArrayList(new AttunedShardJeiRecipe()), WarpPlateJeiRecipeCategory.UID);
//    }
//
//    @Override
//    public void registerCategories(IRecipeCategoryRegistration registry) {
//        registry.addRecipeCategories(new WarpPlateJeiRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
//    }
//}
