package net.blay09.mods.waystones;

import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.block.entity.ModBlockEntities;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.handler.ModEventHandlers;
import net.blay09.mods.waystones.item.ModItems;
import net.blay09.mods.waystones.menu.ModMenus;
import net.blay09.mods.waystones.network.ModNetworking;
import net.blay09.mods.waystones.stats.ModStats;
import net.blay09.mods.waystones.worldgen.ModWorldGen;

public class Waystones {
    public static final String MOD_ID = "waystones";

    public static void initialize() {
        WaystonesConfig.initialize();
        ModStats.initialize();
        ModEventHandlers.initialize();
        ModNetworking.initialize();
        ModBlocks.initialize();
        ModBlockEntities.initialize();
        ModItems.initialize();
        ModMenus.initialize();
        ModWorldGen.initialize();
    }
}
