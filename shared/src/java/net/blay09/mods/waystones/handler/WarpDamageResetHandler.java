package net.blay09.mods.waystones.handler;

import net.blay09.mods.waystones.api.IResetUseOnDamage;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class WarpDamageResetHandler {

    public static void onDamage(LivingEntity entity) {
        if (entity instanceof Player && entity.getUseItem().getItem() instanceof IResetUseOnDamage) {
            entity.stopUsingItem();
        }
    }

}
