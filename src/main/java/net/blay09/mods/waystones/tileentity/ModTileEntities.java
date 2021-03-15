package net.blay09.mods.waystones.tileentity;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Supplier;

public class ModTileEntities {
    public static TileEntityType<WaystoneTileEntity> waystone;
    public static TileEntityType<SharestoneTileEntity> sharestone;

    public static void register(IForgeRegistry<TileEntityType<?>> registry) {
        registry.registerAll(
                waystone = build(WaystoneTileEntity::new, new ResourceLocation(Waystones.MOD_ID, "waystone"), ModBlocks.waystone, ModBlocks.mossyWaystone, ModBlocks.sandyWaystone),
                sharestone = build(SharestoneTileEntity::new, new ResourceLocation(Waystones.MOD_ID, "sharestone"), ModBlocks.sharestone)
        );
    }

    @SuppressWarnings("unchecked")
    private static <T extends TileEntity> TileEntityType<T> build(Supplier<T> factory, ResourceLocation registryName, Block... blocks) {
        //noinspection ConstantConditions dataFixerType can be null apparently
        return (TileEntityType<T>) TileEntityType.Builder.create(factory, blocks).build(null).setRegistryName(registryName);
    }
}
