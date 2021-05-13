package net.blay09.mods.waystones.item;

import net.blay09.mods.waystones.Waystones;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class AttunedShardItem extends Item {

    public static final String name = "attuned_shard";
    public static final ResourceLocation registryName = new ResourceLocation(Waystones.MOD_ID, name);

    public AttunedShardItem() {
        super(new Properties().group(Waystones.itemGroup).maxStackSize(1));
    }

    @Override
    public boolean hasEffect(ItemStack itemStack) {
        return true;
    }

}
