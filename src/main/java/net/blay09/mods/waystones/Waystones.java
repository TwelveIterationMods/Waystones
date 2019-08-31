package net.blay09.mods.waystones;

import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.client.ClientProxy;
import net.blay09.mods.waystones.item.ModItems;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.tileentity.ModTileEntities;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

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

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);

        // TODO WorldGen
        /*VillagerRegistry.instance().registerVillageCreationHandler(new VillageWaystoneCreationHandler());
        MapGenStructureIO.registerStructureComponent(ComponentVillageWaystone.class, "waystones:village_waystone");
        GameRegistry.registerWorldGenerator(new LegacyWorldGen(), 0);*/
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        ModBlocks.register(event.getRegistry());
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

    private void enqueueIMC(InterModEnqueueEvent event) {
        // TODO FMLInterModComms.sendFunctionMessage(Compat.THEONEPROBE, "getTheOneProbe", "net.blay09.mods.waystones.compat.TheOneProbeAddon");
    }

}
