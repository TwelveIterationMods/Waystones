package net.blay09.mods.waystones;

import net.blay09.mods.waystones.item.IResetUseOnDamage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WarpDamageResetHandler {

    @SubscribeEvent
    public void onDamage(LivingDamageEvent event) {
        if (WaystoneConfig.general.resetUseOnDamage) {
            if (event.getEntity() instanceof EntityPlayer && event.getEntityLiving().getActiveItemStack().getItem() instanceof IResetUseOnDamage) {
                event.getEntityLiving().stopActiveHand();
            }
        }
    }

}
