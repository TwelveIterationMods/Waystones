package net.blay09.mods.waystones.datagen;

import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.block.WaystoneBlockBase;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class ModBlockLootTableProvider extends FabricBlockLootTableProvider {
    protected ModBlockLootTableProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        add(ModBlocks.waystone, createDoubleBlockWaystoneLoot(ModBlocks.waystone));
        add(ModBlocks.sandyWaystone, createDoubleBlockWaystoneLoot(ModBlocks.sandyWaystone));
        add(ModBlocks.mossyWaystone, createDoubleBlockWaystoneLoot(ModBlocks.mossyWaystone));
        add(ModBlocks.sharestone, createDoubleBlockWaystoneLoot(ModBlocks.sharestone));
        add(ModBlocks.portstone, createDoubleBlockWaystoneLoot(ModBlocks.portstone));
        add(ModBlocks.warpPlate, createWaystoneLoot(ModBlocks.warpPlate));
        for (Block scopedSharestone : ModBlocks.scopedSharestones) {
            add(scopedSharestone, createDoubleBlockWaystoneLoot(scopedSharestone));
        }
    }

    private LootTable.Builder createWaystoneLoot(Block block) {
        return LootTable.lootTable()
                .withPool(applyExplosionCondition(block, LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1f))
                        .add(LootItem.lootTableItem(block))
                        .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                                .copy("UUID", "UUID", CopyNbtFunction.MergeStrategy.REPLACE)
                                .when(HAS_SILK_TOUCH))));
    }

    private LootTable.Builder createDoubleBlockWaystoneLoot(Block block) {
        return LootTable.lootTable()
                .withPool(applyExplosionCondition(block, LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1f))
                        .add(LootItem.lootTableItem(block))
                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(WaystoneBlockBase.HALF, DoubleBlockHalf.LOWER)))
                        .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                                .copy("UUID", "UUID", CopyNbtFunction.MergeStrategy.REPLACE)
                                .when(HAS_SILK_TOUCH))));
    }
}
