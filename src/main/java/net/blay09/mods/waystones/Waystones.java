package net.blay09.mods.waystones;

import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.client.ClientProxy;
import net.blay09.mods.waystones.client.ModRenderers;
import net.blay09.mods.waystones.item.ModItems;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.tileentity.ModTileEntities;
import net.blay09.mods.waystones.worldgen.ModFeatures;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;

import java.util.Collections;

@Mod(Waystones.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class Waystones {

    public static final String MOD_ID = "waystones";

    public static CommonProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    public static final ItemGroup itemGroup = new ItemGroup(Waystones.MOD_ID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModBlocks.waystone);
        }
    };

    public Waystones() {
        DeferredWorkQueue.runLater(NetworkHandler::init);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, WaystoneConfig.commonSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, WaystoneConfig.serverSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, WaystoneConfig.clientSpec);
    }

    @SubscribeEvent
    public static void setup(FMLCommonSetupEvent event) {
        Biome.BIOMES.forEach(it -> {
            CountRangeConfig placementConfig = new CountRangeConfig(10, 0,0, 1);
            ConfiguredFeature<?> configuredFeature = Biome.createDecoratedFeature(ModFeatures.waystone, NoFeatureConfig.NO_FEATURE_CONFIG, Placement.COUNT_RANGE, placementConfig);
            it.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, configuredFeature);
        });

        ResourceLocation villageStructure = new ResourceLocation("waystones", "village_waystone");
        ResourceLocation emptyStructure = new ResourceLocation("empty");
        JigsawManager.REGISTRY.register(new JigsawPattern(villageStructure, emptyStructure, Collections.emptyList(), JigsawPattern.PlacementBehaviour.RIGID));
    }

    @SubscribeEvent
    public static void setupClient(FMLClientSetupEvent event) {
        ModRenderers.registerRenderers();
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        ModBlocks.register(event.getRegistry());
    }

    @SubscribeEvent
    public static void registerFeature(RegistryEvent.Register<Feature<?>> event) {
        ModFeatures.register(event.getRegistry());
    }

    @SubscribeEvent
    public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {
        ModTileEntities.register(event.getRegistry());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        ModItems.register(event.getRegistry());
        ModBlocks.registerBlockItems(event.getRegistry());
    }

    @SubscribeEvent
    public static void enqueueIMC(InterModEnqueueEvent event) {
        // TODO FMLInterModComms.sendFunctionMessage(Compat.THEONEPROBE, "getTheOneProbe", "net.blay09.mods.waystones.compat.TheOneProbeAddon");
    }

}
