package net.blay09.mods.waystones;

import net.blay09.mods.waystones.block.BlockWaystone;
import net.blay09.mods.waystones.block.TileWaystone;
import net.blay09.mods.waystones.compat.Compat;
import net.blay09.mods.waystones.item.ItemReturnScroll;
import net.blay09.mods.waystones.item.ItemWarpScroll;
import net.blay09.mods.waystones.item.ItemWarpStone;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.worldgen.WaystoneWorldGen;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = Waystones.MOD_ID, name = "Waystones", acceptedMinecraftVersions = "[1.12]")
public class Waystones {

	public static final String MOD_ID = "waystones";

	@Mod.Instance(MOD_ID)
	public static Waystones instance;

	@SidedProxy(serverSide = "net.blay09.mods.waystones.CommonProxy", clientSide = "net.blay09.mods.waystones.client.ClientProxy")
	public static CommonProxy proxy;

	public static BlockWaystone blockWaystone;
	public static ItemReturnScroll itemReturnScroll;
	public static ItemWarpScroll itemWarpScroll;
	public static ItemWarpStone itemWarpStone;

	public static final CreativeTabs creativeTab = new CreativeTabs(Waystones.MOD_ID) {
		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(Waystones.blockWaystone);
		}
	};

	public static Configuration configuration;

	private WaystoneConfig config;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		blockWaystone = new BlockWaystone();
		GameRegistry.register(blockWaystone);
		GameRegistry.register(new ItemBlock(blockWaystone).setRegistryName(blockWaystone.getRegistryName()));
		GameRegistry.registerTileEntity(TileWaystone.class, MOD_ID + ":waystone");

		itemReturnScroll = new ItemReturnScroll();
		GameRegistry.register(itemReturnScroll);

		itemWarpScroll = new ItemWarpScroll();
		GameRegistry.register(itemWarpScroll);

		itemWarpStone = new ItemWarpStone();
		GameRegistry.register(itemWarpStone);

		NetworkHandler.init();
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);

		configuration = new Configuration(event.getSuggestedConfigurationFile());
		config = new WaystoneConfig();
		config.reloadLocal(configuration);
		if(configuration.hasChanged()) {
			configuration.save();
		}

		if(WaystoneConfig.worldGenChance > 0) {
			GameRegistry.registerWorldGenerator(new WaystoneWorldGen(), 0);
		}

		proxy.preInit(event);

// 		Sad:
//		CraftingHelper.register(new ResourceLocation(MOD_ID, "allow_return_scroll"), (IConditionFactory) (context, json) -> () -> instance.config.allowReturnScrolls);
//		CraftingHelper.register(new ResourceLocation(MOD_ID, "allow_warp_scroll"), (IConditionFactory) (context, json) -> () -> instance.config.allowWarpScrolls);
//		CraftingHelper.register(new ResourceLocation(MOD_ID, "allow_warp_stone"), (IConditionFactory) (context, json) -> () -> instance.config.allowWarpStone);
//		CraftingHelper.register(new ResourceLocation(MOD_ID, "allow_waystone"), (IConditionFactory) (context, json) -> () -> !instance.config.creativeModeOnly);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		FMLInterModComms.sendFunctionMessage(Compat.THEONEPROBE, "getTheOneProbe", "net.blay09.mods.waystones.compat.TheOneProbeAddon");
	}

	public static WaystoneConfig getConfig() {
		return instance.config;
	}

	public void setConfig(WaystoneConfig config) {
		this.config = config;
	}

}
