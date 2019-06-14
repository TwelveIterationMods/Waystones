package net.blay09.mods.waystones;

import net.blay09.mods.waystones.block.BlockWaystone;
import net.blay09.mods.waystones.block.TileWaystone;
import net.blay09.mods.waystones.client.ClientProxy;
import net.blay09.mods.waystones.compat.Compat;
import net.blay09.mods.waystones.item.ItemBoundScroll;
import net.blay09.mods.waystones.item.ItemReturnScroll;
import net.blay09.mods.waystones.item.ItemWarpScroll;
import net.blay09.mods.waystones.item.ItemWarpStone;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.worldgen.ComponentVillageWaystone;
import net.blay09.mods.waystones.worldgen.LegacyWorldGen;
import net.blay09.mods.waystones.worldgen.VillageWaystoneCreationHandler;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(Waystones.MOD_ID)
@Mod.EventBusSubscriber
public class Waystones {

    public static final String MOD_ID = "waystones";

    @SuppressWarnings("Convert2MethodRef")
    public static CommonProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new CommonProxy());

    public static Block blockWaystone;
    public static Item itemReturnScroll;
    public static Item itemBoundScroll;
    public static Item itemWarpScroll;
    public static Item itemWarpStone;

    public static final ItemGroup itemGroup = new ItemGroup(Waystones.MOD_ID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Waystones.blockWaystone);
        }
    };

    public Waystones() {

    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        GameRegistry.registerTileEntity(TileWaystone.class, new ResourceLocation(MOD_ID, "waystone"));

        NetworkHandler.init();

        VillagerRegistry.instance().registerVillageCreationHandler(new VillageWaystoneCreationHandler());
        MapGenStructureIO.registerStructureComponent(ComponentVillageWaystone.class, "waystones:village_waystone");
        GameRegistry.registerWorldGenerator(new LegacyWorldGen(), 0);

        proxy.preInit(event);

        MinecraftForge.EVENT_BUS.register(new WarpDamageResetHandler());
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
                new BlockWaystone()
        );
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                new ItemBlock(blockWaystone).setRegistryName(BlockWaystone.name)
        );

        event.getRegistry().registerAll(
                new ItemReturnScroll().setRegistryName(ItemReturnScroll.registryName),
                new ItemBoundScroll().setRegistryName(ItemBoundScroll.registryName),
                new ItemWarpScroll(),
                new ItemWarpStone()
        );
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(Waystones.blockWaystone), 0, TileWaystone.class);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        FMLInterModComms.sendFunctionMessage(Compat.THEONEPROBE, "getTheOneProbe", "net.blay09.mods.waystones.compat.TheOneProbeAddon");
    }

}
