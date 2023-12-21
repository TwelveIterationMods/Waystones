package net.blay09.mods.waystones.datagen;

import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.block.WaystoneBlockBase;
import net.blay09.mods.waystones.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.Optional;

import static net.minecraft.data.models.BlockModelGenerators.createHorizontalFacingDispatch;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        blockStateModelGenerator.createNonTemplateModelBlock(ModBlocks.warpPlate);
        blockStateModelGenerator.createNonTemplateModelBlock(ModBlocks.landingStone);
        createDoubleBlockWaystone(blockStateModelGenerator, ModBlocks.waystone);
        createDoubleBlockWaystone(blockStateModelGenerator, ModBlocks.sandyWaystone);
        createDoubleBlockWaystone(blockStateModelGenerator, ModBlocks.mossyWaystone);
        createDoubleBlockWaystone(blockStateModelGenerator, ModBlocks.deepslateWaystone);
        createDoubleBlockWaystone(blockStateModelGenerator, ModBlocks.blackstoneWaystone);
        createDoubleBlockWaystone(blockStateModelGenerator, ModBlocks.endStoneWaystone);
        createDoubleBlockWaystone(blockStateModelGenerator, ModBlocks.portstone);
        createDoubleBlockWaystone(blockStateModelGenerator, ModBlocks.sharestone);
        for (Block scopedSharestone : ModBlocks.scopedSharestones) {
            createDoubleBlockWaystone(blockStateModelGenerator, scopedSharestone, ModBlocks.sharestone);
        }
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        itemModelGenerator.generateFlatItem(ModItems.warpDust, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.attunedShard, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.crumblingAttunedShard, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.warpStone, ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.warpScroll, ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.returnScroll, ModelTemplates.FLAT_HANDHELD_ITEM);

        final var dyedSharestoneTemplate = new ModelTemplate(Optional.of(new ResourceLocation("waystones", "item/dyed_sharestone")), Optional.empty());
        for (Block scopedSharestone : ModBlocks.scopedSharestones) {
            itemModelGenerator.generateFlatItem(scopedSharestone.asItem(), dyedSharestoneTemplate);
        }
    }

    private void createDoubleBlockWaystone(BlockModelGenerators blockStateModelGenerator, Block block) {
        createDoubleBlockWaystone(blockStateModelGenerator, block, block);
    }

    private void createDoubleBlockWaystone(BlockModelGenerators blockStateModelGenerator, Block block, Block modelBlock) {
        final var topModelLocation = ModelLocationUtils.getModelLocation(modelBlock, "_top");
        final var bottomModelLocation = ModelLocationUtils.getModelLocation(modelBlock, "_bottom");
        final var generator = MultiVariantGenerator.multiVariant(block)
                .with(createHorizontalFacingDispatch())
                .with(PropertyDispatch.property(WaystoneBlockBase.HALF)
                        .select(DoubleBlockHalf.LOWER, Variant.variant().with(VariantProperties.MODEL, bottomModelLocation))
                        .select(DoubleBlockHalf.UPPER, Variant.variant().with(VariantProperties.MODEL, topModelLocation)));
        blockStateModelGenerator.blockStateOutput.accept(generator);
        blockStateModelGenerator.skipAutoItemBlock(block);
    }

}
