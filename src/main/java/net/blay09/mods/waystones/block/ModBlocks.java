package net.blay09.mods.waystones.block;

import net.blay09.mods.waystones.Waystones;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

public class ModBlocks {
    public static Block waystone;
    public static Block mossyWaystone;
    public static Block sandyWaystone;
    public static Block sharestone;
    public static Block warpPlate;

    public static void register(IForgeRegistry<Block> registry) {
        registry.registerAll(
                waystone = new WaystoneBlock().setRegistryName("waystone"),
                mossyWaystone = new WaystoneBlock().setRegistryName("mossy_waystone"),
                sandyWaystone = new WaystoneBlock().setRegistryName("sandy_waystone"),
                sharestone = new SharestoneBlock().setRegistryName("sharestone"),
                warpPlate = new WarpPlateBlock().setRegistryName("warp_plate")
        );
    }

    public static void registerBlockItems(IForgeRegistry<Item> registry) {
        registry.registerAll(
                new BlockItem(waystone, new Item.Properties().group(Waystones.itemGroup)).setRegistryName("waystone"),
                new BlockItem(mossyWaystone, new Item.Properties().group(Waystones.itemGroup)).setRegistryName("mossy_waystone"),
                new BlockItem(sandyWaystone, new Item.Properties().group(Waystones.itemGroup)).setRegistryName("sandy_waystone"),
                new BlockItem(sharestone, new Item.Properties().group(Waystones.itemGroup)).setRegistryName("sharestone"),
                new BlockItem(warpPlate, new Item.Properties().group(Waystones.itemGroup)).setRegistryName("warp_plate")
        );
    }

}
