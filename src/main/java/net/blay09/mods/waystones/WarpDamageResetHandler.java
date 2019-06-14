package net.blay09.mods.waystones;

import net.blay09.mods.waystones.item.IResetUseOnDamage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class WarpDamageResetHandler {

    @SubscribeEvent
    public void onDamage(LivingDamageEvent event) {
        if (WaystoneConfig.general.resetUseOnDamage) {
            if (event.getEntity() instanceof PlayerEntity && event.getEntityLiving().getActiveItemStack().getItem() instanceof IResetUseOnDamage) {
                event.getEntityLiving().stopActiveHand();
            }
        }
    }

}
