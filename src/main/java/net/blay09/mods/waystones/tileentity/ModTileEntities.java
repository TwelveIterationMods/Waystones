package net.blay09.mods.waystones.tileentity;

import net.blay09.mods.waystones.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Supplier;

public class ModTileEntities {
    public static TileEntityType<WaystoneTileEntity> waystone;

    public static void register(IForgeRegistry<TileEntityType<?>> registry) {
        registry.registerAll(
                waystone = build(WaystoneTileEntity::new, ModBlocks.waystone)
        );
    }

    @SuppressWarnings("unchecked")
    private static <T extends TileEntity> TileEntityType<T> build(Supplier<T> factory, Block block) {
        ResourceLocation registryName = block.getRegistryName();
        if (registryName == null) {
            throw new IllegalArgumentException("Block passed into tile entity registration is not registered correctly");
        }

        //noinspection ConstantConditions dataFixerType can be null apparently
        return (TileEntityType<T>) TileEntityType.Builder.create(factory, block).build(null).setRegistryName(registryName);
    }
}
