package net.blay09.mods.waystones.block;

import net.blay09.mods.waystones.Waystones;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Objects;

public class ModBlocks {
    public static Block waystone;
    public static Block mossyWaystone;
    public static Block sandyWaystone;
    public static Block sharestone;
    public static SharestoneBlock[] scopedSharestones;
    public static Block warpPlate;
    public static Block portstone;

    public static void register(IForgeRegistry<Block> registry) {
        registry.registerAll(
                waystone = new WaystoneBlock().setRegistryName("waystone"),
                mossyWaystone = new WaystoneBlock().setRegistryName("mossy_waystone"),
                sandyWaystone = new WaystoneBlock().setRegistryName("sandy_waystone"),
                warpPlate = new WarpPlateBlock().setRegistryName("warp_plate"),
                sharestone = new SharestoneBlock(null).setRegistryName("sharestone"),
                portstone = new PortstoneBlock().setRegistryName("portstone")
        );

        DyeColor[] colors = DyeColor.values();
        scopedSharestones = new SharestoneBlock[16];
        for (DyeColor color : colors) {
            scopedSharestones[color.ordinal()] = new SharestoneBlock(color);
            registry.register(scopedSharestones[color.ordinal()].setRegistryName(color.getTranslationKey() + "_sharestone"));
        }
    }

    public static void registerBlockItems(IForgeRegistry<Item> registry) {
        registry.registerAll(
                new BlockItem(waystone, new Item.Properties().group(Waystones.itemGroup)).setRegistryName("waystone"),
                new BlockItem(mossyWaystone, new Item.Properties().group(Waystones.itemGroup)).setRegistryName("mossy_waystone"),
                new BlockItem(sandyWaystone, new Item.Properties().group(Waystones.itemGroup)).setRegistryName("sandy_waystone"),
                new BlockItem(sharestone, new Item.Properties().group(Waystones.itemGroup)).setRegistryName("sharestone"),
                new BlockItem(warpPlate, new Item.Properties().group(Waystones.itemGroup)).setRegistryName("warp_plate"),
                new BlockItem(portstone, new Item.Properties().group(Waystones.itemGroup)).setRegistryName("portstone")
        );
        for (SharestoneBlock scopedSharestone : scopedSharestones) {
            registry.register(new BlockItem(scopedSharestone, new Item.Properties().group(Waystones.itemGroup))
                    .setRegistryName(Objects.requireNonNull(scopedSharestone.getColor()).getTranslationKey() + "_sharestone"));
        }
    }

}
