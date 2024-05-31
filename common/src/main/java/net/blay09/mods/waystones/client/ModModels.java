package net.blay09.mods.waystones.client;

import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.client.rendering.BalmModels;
import net.blay09.mods.waystones.Waystones;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;

public class ModModels {
    public static DeferredObject<BakedModel> portstoneRunes;

    public static void initialize(BalmModels models) {
        portstoneRunes = models.loadModel(id("block/portstone_runes"));
    }

    private static ResourceLocation id(String path) {
        return new ResourceLocation(Waystones.MOD_ID, path);
    }
}