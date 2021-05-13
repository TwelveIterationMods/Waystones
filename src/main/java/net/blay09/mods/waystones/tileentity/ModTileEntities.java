package net.blay09.mods.waystones.tileentity;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.function.Supplier;

public class ModTileEntities {
    public static TileEntityType<WaystoneTileEntity> waystone;
    public static TileEntityType<SharestoneTileEntity> sharestone;
    public static TileEntityType<WarpPlateTileEntity> warpPlate;

    public static void register(IForgeRegistry<TileEntityType<?>> registry) {
        Block[] sharestones = ArrayUtils.add(ModBlocks.scopedSharestones, ModBlocks.sharestone);
        registry.registerAll(
                waystone = build(WaystoneTileEntity::new, new ResourceLocation(Waystones.MOD_ID, "waystone"), ModBlocks.waystone, ModBlocks.mossyWaystone, ModBlocks.sandyWaystone),
                sharestone = build(SharestoneTileEntity::new, new ResourceLocation(Waystones.MOD_ID, "sharestone"), sharestones),
                warpPlate = build(WarpPlateTileEntity::new, new ResourceLocation(Waystones.MOD_ID, "warp_plate"), ModBlocks.warpPlate)
        );
    }

    @SuppressWarnings("unchecked")
    private static <T extends TileEntity> TileEntityType<T> build(Supplier<T> factory, ResourceLocation registryName, Block... blocks) {
        //noinspection ConstantConditions dataFixerType can be null apparently
        return (TileEntityType<T>) TileEntityType.Builder.create(factory, blocks).build(null).setRegistryName(registryName);
    }
}
