package net.blay09.mods.waystones.block;

import net.blay09.mods.waystones.Waystones;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

public class ModBlocks {
    public static WaystoneBlock waystone;

    public static void register(IForgeRegistry<Block> registry) {
        registry.registerAll(
                waystone = new WaystoneBlock().setRegistryName(WaystoneBlock.registryName)
        );
    }

    public static void registerBlockItems(IForgeRegistry<Item> registry) {
        registry.registerAll(
                new BlockItem(waystone, new Item.Properties().group(Waystones.itemGroup)).setRegistryName(WaystoneBlock.registryName)
        );
    }

}
