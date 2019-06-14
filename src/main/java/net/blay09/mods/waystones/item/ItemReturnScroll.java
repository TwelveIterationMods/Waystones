package net.blay09.mods.waystones.item;

import net.blay09.mods.waystones.PlayerWaystoneHelper;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class ItemReturnScroll extends ItemBoundScroll {

    public static final String name = "return_scroll";
    public static final ResourceLocation registryName = new ResourceLocation(Waystones.MOD_ID, name);

    public ItemReturnScroll() {
        setUnlocalizedName(registryName.toString());
    }

    @Nullable
    @Override
    protected WaystoneEntry getBoundTo(PlayerEntity player, ItemStack itemStack) {
        return PlayerWaystoneHelper.getLastWaystone(player);
    }

}
