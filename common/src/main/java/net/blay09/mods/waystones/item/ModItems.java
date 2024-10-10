package net.blay09.mods.waystones.item;


import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.item.BalmItems;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ModItems {
    public static DeferredObject<CreativeModeTab> creativeModeTab;

    public static Item returnScroll;
    public static Item boundScroll;
    public static Item warpScroll;
    public static Item warpStone;
    public static Item warpDust;
    public static Item dormantShard;
    public static Item attunedShard;
    public static Item deepslateShard;
    public static Item crumblingAttunedShard;

    public static void initialize(BalmItems items) {
        items.registerItem(() -> returnScroll = new ReturnScrollItem(defaultProperties("return_scroll")), id("return_scroll"));
        items.registerItem(() -> boundScroll = new BoundScrollItem(defaultProperties("bound_scroll")), id("bound_scroll"), null);
        items.registerItem(() -> warpScroll = new WarpScrollItem(defaultProperties("warp_scroll")), id("warp_scroll"));
        items.registerItem(() -> warpStone = new WarpStoneItem(defaultProperties("warp_stone")), id("warp_stone"));
        items.registerItem(() -> warpDust = new WarpDustItem(defaultProperties("warp_dust")), id("warp_dust"));
        items.registerItem(() -> dormantShard = new ShardItem(defaultProperties("dormant_shard")), id("dormant_shard"));
        items.registerItem(() -> attunedShard = new AttunedShardItem(defaultProperties("attuned_shard")), id("attuned_shard"), null);
        items.registerItem(() -> deepslateShard = new ShardItem(defaultProperties("deepslate_shard")), id("deepslate_shard"));
        items.registerItem(() -> crumblingAttunedShard = new CrumblingAttunedShardItem(defaultProperties("crumbling_attuned_shard")), id("crumbling_attuned_shard"), null);

        creativeModeTab = items.registerCreativeModeTab(() -> new ItemStack(ModBlocks.waystone), id("waystones"));

        items.setCreativeModeTabSorting(id("waystones"), new Comparator<>() {
            private static final String[] patternStrings = new String[]{
                    "waystone",
                    "white_portstone",
                    "red_sharestone",
                    "warp_plate",
                    "return_scroll",
                    "warp_scroll",
                    "warp_stone",
                    "warp_dust",
                    "dormant_shard",
                    "deepslate_shard",
                    ".+_waystone",
                    ".+_sharestone",
                    ".+_portstone",
                    "bound_scroll",
                    "attuned_shard",
                    "crumbling_attuned_shard",
            };

            private static final Map<String, Integer> indexMap = new HashMap<>();
            private static final Map<Pattern, Integer> patternIndexMap = new HashMap<>();

            static {
                for (int i = 0; i < patternStrings.length; i++) {
                    final var patternString = patternStrings[i];
                    indexMap.put(patternString, i);
                    patternIndexMap.put(Pattern.compile(patternString), i);
                }
            }

            private static int getIndex(String name) {
                final var index = indexMap.get(name);
                if (index != null) {
                    return index;
                }

                for (var entry : patternIndexMap.entrySet()) {
                    if (entry.getKey().matcher(name).matches()) {
                        return entry.getValue();
                    }
                }

                return -1;
            }

            @Override
            public int compare(ItemLike o1, ItemLike o2) {
                final var id1 = BuiltInRegistries.ITEM.getKey(o1.asItem());
                final var id2 = BuiltInRegistries.ITEM.getKey(o2.asItem());
                final var name1 = id1.getPath();
                final var name2 = id2.getPath();
                final var index1 = getIndex(name1);
                final var index2 = getIndex(name2);
                if (index1 != -1 && index2 != -1) {
                    return Integer.compare(index1, index2);
                } else if (index1 != -1) {
                    return -1;
                } else if (index2 != -1) {
                    return 1;
                }

                return name1.compareTo(name2);
            }
        });
    }

    private static Item.Properties defaultProperties(String name) {
        return Balm.getItems().itemProperties().setId(itemId(name));
    }

    private static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(Waystones.MOD_ID, name);
    }

    private static ResourceKey<Item> itemId(String name) {
        return ResourceKey.create(Registries.ITEM, id(name));
    }
}
