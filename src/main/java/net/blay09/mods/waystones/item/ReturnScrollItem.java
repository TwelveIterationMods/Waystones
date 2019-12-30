package net.blay09.mods.waystones.item;

import net.blay09.mods.waystones.PlayerWaystoneHelper;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.core.IWaystone;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class ReturnScrollItem extends BoundScrollItem {

    public static final String name = "return_scroll";
    public static final ResourceLocation registryName = new ResourceLocation(Waystones.MOD_ID, name);

    @Nullable
    @Override
    protected IWaystone getBoundTo(PlayerEntity player, ItemStack itemStack) {
        return PlayerWaystoneHelper.getNearestWaystone(player);
    }

}
