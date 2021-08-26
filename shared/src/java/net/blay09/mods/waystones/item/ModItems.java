package net.blay09.mods.waystones.item;


import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.item.BalmItems;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.ModBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ModItems {
    public static final CreativeModeTab creativeModeTab = Balm.getItems().createCreativeModeTab(id("waystones"), () -> new ItemStack(ModBlocks.waystone));

    public static final Item returnScroll = new ReturnScrollItem(Balm.getItems().itemProperties(creativeModeTab));
    public static final Item boundScroll = new BoundScrollItem(Balm.getItems().itemProperties(creativeModeTab));
    public static final Item warpScroll = new WarpScrollItem(Balm.getItems().itemProperties(creativeModeTab));
    public static final Item warpStone = new WarpStoneItem(Balm.getItems().itemProperties(creativeModeTab));
    public static final Item warpDust = new WarpDustItem(Balm.getItems().itemProperties(creativeModeTab));
    public static final Item attunedShard = new AttunedShardItem(Balm.getItems().itemProperties(creativeModeTab));

    public static void initialize(BalmItems items) {
        items.registerItem(() -> returnScroll, id("return_scroll"));
        items.registerItem(() -> boundScroll, id("bound_scroll"));
        items.registerItem(() -> warpScroll, id("warp_scroll"));
        items.registerItem(() -> warpStone, id("warp_stone"));
        items.registerItem(() -> warpDust, id("warp_dust"));
        items.registerItem(() -> attunedShard, id("attuned_shard"));
    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(Waystones.MOD_ID, name);
    }
    
}
