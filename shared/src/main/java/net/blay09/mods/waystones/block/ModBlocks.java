package net.blay09.mods.waystones.block;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.block.BalmBlocks;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

public class ModBlocks {

    public static final Block waystone = new WaystoneBlock(defaultProperties());
    public static final Block mossyWaystone = new WaystoneBlock(defaultProperties());
    public static final Block sandyWaystone = new WaystoneBlock(defaultProperties());
    public static final Block sharestone = new SharestoneBlock(defaultProperties(), null);
    public static final Block[] scopedSharestones = new SharestoneBlock[DyeColor.values().length];
    public static final Block warpPlate = new WarpPlateBlock(defaultProperties());
    public static final Block portstone = new PortstoneBlock(defaultProperties());

    public static void initialize(BalmBlocks blocks) {
        blocks.register(() -> waystone, () -> itemBlock(waystone), id("waystone"));
        blocks.register(() -> mossyWaystone, () -> itemBlock(mossyWaystone), id("mossy_waystone"));
        blocks.register(() -> sandyWaystone, () -> itemBlock(sandyWaystone), id("sandy_waystone"));
        blocks.register(() -> warpPlate, () -> itemBlock(warpPlate), id("warp_plate"));
        blocks.register(() -> sharestone, () -> itemBlock(sharestone), id("sharestone"));
        blocks.register(() -> portstone, () -> itemBlock(portstone), id("portstone"));

        DyeColor[] colors = DyeColor.values();
        for (DyeColor color : colors) {
            scopedSharestones[color.ordinal()] = new SharestoneBlock(defaultProperties(), color);
            blocks.register(() -> scopedSharestones[color.ordinal()], () -> itemBlock(scopedSharestones[color.ordinal()]), id(color.getSerializedName() + "_sharestone"));
        }
    }

    private static BlockItem itemBlock(Block block) {
        return new BlockItem(block, Balm.getBlocks().itemProperties(ModItems.creativeModeTab));
    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(Waystones.MOD_ID, name);
    }

    private static BlockBehaviour.Properties defaultProperties() {
        return Balm.getBlocks().blockProperties(Material.STONE).sound(SoundType.STONE).strength(5f, 2000f);
    }
}
