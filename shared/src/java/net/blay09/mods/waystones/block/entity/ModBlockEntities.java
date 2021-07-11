package net.blay09.mods.waystones.block.entity;

import net.blay09.mods.balm.block.entity.BalmBlockEntities;
import net.blay09.mods.balm.core.DeferredObject;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.ModBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.apache.commons.lang3.ArrayUtils;

public class ModBlockEntities extends BalmBlockEntities {
    public static DeferredObject<BlockEntityType<WaystoneBlockEntity>> waystone;
    public static DeferredObject<BlockEntityType<SharestoneBlockEntity>> sharestone;
    public static DeferredObject<BlockEntityType<WarpPlateBlockEntity>> warpPlate;
    public static DeferredObject<BlockEntityType<PortstoneBlockEntity>> portstone;

    public static void initialize() {
        waystone = registerBlockEntity(id("waystone"), WaystoneBlockEntity::new, ModBlocks.waystone, ModBlocks.mossyWaystone, ModBlocks.sandyWaystone);
        sharestone = registerBlockEntity(id("sharestone"), SharestoneBlockEntity::new, ArrayUtils.add(ModBlocks.scopedSharestones, ModBlocks.sharestone));
        warpPlate = registerBlockEntity(id("warp_plate"), WarpPlateBlockEntity::new, ModBlocks.warpPlate);
        portstone = registerBlockEntity(id("portstone"), PortstoneBlockEntity::new, ModBlocks.portstone);
    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(Waystones.MOD_ID, name);
    }

}
