package net.blay09.mods.waystones.handler;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.item.IFOVOnUse;
import net.blay09.mods.waystones.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Waystones.MOD_ID, value = Dist.CLIENT)
public class WarpStoneFOVHandler {

    @SubscribeEvent
    public static void onFOV(FOVUpdateEvent event) {
        ItemStack activeItemStack = event.getEntity().getActiveItemStack();
        if (isScrollItem(activeItemStack)) {
            float fov = event.getEntity().getItemInUseCount() / 64f * 2f + 0.5f;
            event.setNewfov(MathHelper.lerp(Minecraft.getInstance().gameSettings.fovScaleEffect, 1f, fov));
        }
    }

    private static boolean isScrollItem(ItemStack activeItemStack) {
        return !activeItemStack.isEmpty() && activeItemStack.getItem() instanceof IFOVOnUse;
    }

}
