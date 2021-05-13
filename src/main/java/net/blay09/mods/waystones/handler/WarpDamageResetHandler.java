package net.blay09.mods.waystones.handler;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.IResetUseOnDamage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Waystones.MOD_ID)
public class WarpDamageResetHandler {

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent event) {
        if (event.getEntity() instanceof PlayerEntity && event.getEntityLiving().getActiveItemStack().getItem() instanceof IResetUseOnDamage) {
            event.getEntityLiving().stopActiveHand();
        }
    }

}
