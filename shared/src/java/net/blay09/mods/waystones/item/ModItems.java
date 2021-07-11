package net.blay09.mods.waystones.item;


import net.blay09.mods.balm.item.BalmItems;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.ModBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ModItems extends BalmItems {
    public static final CreativeModeTab creativeModeTab = createCreativeModeTab(id("waystones"), () -> new ItemStack(ModBlocks.waystone));

    public static final Item returnScroll = new ReturnScrollItem(itemProperties(creativeModeTab));
    public static final Item boundScroll = new BoundScrollItem(itemProperties(creativeModeTab));
    public static final Item warpScroll = new WarpScrollItem(itemProperties(creativeModeTab));
    public static final Item warpStone = new WarpStoneItem(itemProperties(creativeModeTab));
    public static final Item warpDust = new WarpDustItem(itemProperties(creativeModeTab));
    public static final Item attunedShard = new AttunedShardItem(itemProperties(creativeModeTab));

    public static void initialize() {
        registerItem(() -> returnScroll, id("return_scroll"));
        registerItem(() -> boundScroll, id("bound_scroll"));
        registerItem(() -> warpScroll, id("warp_scroll"));
        registerItem(() -> warpStone, id("warp_stone"));
        registerItem(() -> warpDust, id("warp_dust"));
        registerItem(() -> attunedShard, id("attuned_shard"));
    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(Waystones.MOD_ID, name);
    }
    
}
