package net.blay09.mods.waystones.block;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.block.BalmBlocks;
import net.blay09.mods.waystones.Waystones;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.Nullable;

public class ModBlocks {

    private static final DyeColor[] portstoneColors = new DyeColor[]{
            DyeColor.WHITE,
            DyeColor.ORANGE,
            DyeColor.MAGENTA,
            DyeColor.LIGHT_BLUE,
            DyeColor.YELLOW,
            DyeColor.LIME,
            DyeColor.PINK,
            DyeColor.GRAY,
            DyeColor.LIGHT_GRAY,
            DyeColor.CYAN,
            DyeColor.PURPLE,
            DyeColor.BLUE,
            DyeColor.BROWN,
            DyeColor.GREEN,
            DyeColor.RED,
            DyeColor.BLACK
    };

    private static final DyeColor[] sharestoneColors = new DyeColor[]{
            DyeColor.ORANGE,
            DyeColor.MAGENTA,
            DyeColor.LIGHT_BLUE,
            DyeColor.YELLOW,
            DyeColor.LIME,
            DyeColor.PINK,
            DyeColor.GRAY,
            DyeColor.LIGHT_GRAY,
            DyeColor.CYAN,
            DyeColor.PURPLE,
            DyeColor.BLUE,
            DyeColor.BROWN,
            DyeColor.GREEN,
            DyeColor.RED,
            DyeColor.BLACK
    };

    public static Block waystone;
    public static Block mossyWaystone;
    public static Block sandyWaystone;
    public static Block deepslateWaystone;
    public static Block blackstoneWaystone;
    public static Block endStoneWaystone;
    public static Block warpPlate;
    public static final PortstoneBlock[] portstones = new PortstoneBlock[portstoneColors.length];
    public static final SharestoneBlock[] sharestones = new SharestoneBlock[sharestoneColors.length];

    public static void initialize(BalmBlocks blocks) {
        blocks.register(() -> waystone = new WaystoneBlock(defaultProperties("waystone")), () -> itemBlock(waystone, "waystone"), id("waystone"));
        blocks.register(() -> mossyWaystone = new WaystoneBlock(defaultProperties("mossy_waystone")), () -> itemBlock(mossyWaystone, "mossy_waystone"), id("mossy_waystone"));
        blocks.register(() -> sandyWaystone = new WaystoneBlock(defaultProperties("sandy_waystone")), () -> itemBlock(sandyWaystone, "sandy_waystone"), id("sandy_waystone"));
        blocks.register(() -> deepslateWaystone = new WaystoneBlock(defaultProperties("deepslate_waystone").sound(SoundType.DEEPSLATE)),
                () -> itemBlock(deepslateWaystone, "deepslate_waystone"),
                id("deepslate_waystone"));
        blocks.register(() -> blackstoneWaystone = new WaystoneBlock(defaultProperties("blackstone_waystone")), () -> itemBlock(blackstoneWaystone, "blackstone_waystone"), id("blackstone_waystone"));
        blocks.register(() -> endStoneWaystone = new WaystoneBlock(defaultProperties("end_stone_waystone")), () -> itemBlock(endStoneWaystone, "end_stone_waystone"), id("end_stone_waystone"));
        blocks.register(() -> warpPlate = new WarpPlateBlock(defaultProperties("warp_plate")), () -> itemBlock(warpPlate, "warp_plate"), id("warp_plate"));

        for (final var color : portstoneColors) {
            blocks.register(() -> portstones[color.ordinal()] = new PortstoneBlock(color, defaultProperties(color.getSerializedName() + "_portstone")),
                    () -> itemBlock(portstones[color.ordinal()], color.getSerializedName() + "_portstone"),
                    id(color.getSerializedName() + "_portstone"));
        }

        for (final var color : sharestoneColors) {
            blocks.register(() -> sharestones[color.ordinal() - 1] = new SharestoneBlock(color, defaultProperties(color.getSerializedName() + "_sharestone")),
                    () -> itemBlock(sharestones[color.ordinal() - 1], color.getSerializedName() + "_sharestone"),
                    id(color.getSerializedName() + "_sharestone"));
        }
    }

    private static BlockItem itemBlock(Block block, String name) {
        return new BlockItem(block, defaultItemProperties(name));
    }

    private static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(Waystones.MOD_ID, name);
    }

    private static ResourceKey<Block> blockId(String name) {
        return ResourceKey.create(Registries.BLOCK, id(name));
    }

    private static ResourceKey<Item> itemId(String name) {
        return ResourceKey.create(Registries.ITEM, id(name));
    }

    private static BlockBehaviour.Properties defaultProperties(String name) {
        return Balm.getBlocks().blockProperties().setId(blockId(name)).sound(SoundType.STONE).strength(5f, 2000f);
    }

    private static Item.Properties defaultItemProperties(String name) {
        return Balm.getItems().itemProperties().setId(itemId(name));
    }

    @Nullable
    public static SharestoneBlock getSharestone(DyeColor color) {
        final var index = color.ordinal() - 1; // -1 because we skip WHITE
        if (index < 0 || index >= sharestones.length) {
            return null;
        }

        return sharestones[index];
    }

    @Nullable
    public static PortstoneBlock getPortstone(DyeColor color) {
        final var index = color.ordinal();
        if (index >= portstones.length) {
            return null;
        }

        return portstones[index];
    }

}
