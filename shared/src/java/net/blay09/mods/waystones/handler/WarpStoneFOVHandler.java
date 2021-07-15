package net.blay09.mods.waystones.handler;

import net.blay09.mods.waystones.api.IFOVOnUse;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class WarpStoneFOVHandler {

    public static Float onFOV(LivingEntity entity) {
        ItemStack activeItemStack = entity.getUseItem();
        if (isScrollItem(activeItemStack)) {
            float fov = entity.getUseItemRemainingTicks() / 32f * 2f;
            return Mth.lerp(Minecraft.getInstance().options.fovEffectScale, 1f, fov);
        }

        return null;
    }

    private static boolean isScrollItem(ItemStack activeItemStack) {
        return !activeItemStack.isEmpty() && activeItemStack.getItem() instanceof IFOVOnUse;
    }

}
