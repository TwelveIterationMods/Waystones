package net.blay09.mods.waystones.item;

import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

public class ModItems {
    public static Item returnScroll;
    public static Item boundScroll;
    public static Item warpScroll;
    public static Item warpStone;
    public static Item warpDust;
    public static Item attunedShard;

    public static void register(IForgeRegistry<Item> registry) {
        registry.registerAll(
                returnScroll = new ReturnScrollItem().setRegistryName(ReturnScrollItem.registryName),
                boundScroll = new BoundScrollItem().setRegistryName(BoundScrollItem.registryName),
                warpScroll = new WarpScrollItem().setRegistryName(WarpScrollItem.registryName),
                warpStone = new WarpStoneItem().setRegistryName(WarpStoneItem.registryName),
                warpDust = new WarpDustItem().setRegistryName(WarpDustItem.registryName),
                attunedShard = new AttunedShardItem().setRegistryName(AttunedShardItem.registryName)
        );
    }
}
