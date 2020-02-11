package net.blay09.mods.waystones.handler;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.item.IFOVOnUse;
import net.blay09.mods.waystones.item.ModItems;
import net.minecraft.item.ItemStack;
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
            event.setNewfov(event.getEntity().getItemInUseCount() / 64f * 2f + 0.5f);
        }
    }

    private static boolean isScrollItem(ItemStack activeItemStack) {
        return !activeItemStack.isEmpty() && activeItemStack.getItem() instanceof IFOVOnUse;
    }

}
