package net.blay09.mods.waystones;

import net.blay09.mods.waystones.block.BlockWaystone;
import net.blay09.mods.waystones.block.TileWaystone;
import net.blay09.mods.waystones.item.ItemReturnScroll;
import net.blay09.mods.waystones.item.ItemWarpStone;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

@Mod(modid = Waystones.MOD_ID, name = "Waystones", acceptedMinecraftVersions = "[1.10]")
@SuppressWarnings("unused")
public class Waystones {

	public static final String MOD_ID = "waystones";

	@Mod.Instance(MOD_ID)
	public static Waystones instance;

	@SidedProxy(serverSide = "net.blay09.mods.waystones.CommonProxy", clientSide = "net.blay09.mods.waystones.client.ClientProxy")
	public static CommonProxy proxy;

	public static BlockWaystone blockWaystone;
	public static ItemReturnScroll itemReturnScroll;
	public static ItemWarpStone itemWarpStone;

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

		itemWarpStone = new ItemWarpStone();
		GameRegistry.register(itemWarpStone);

		NetworkHandler.init();

		configuration = new Configuration(event.getSuggestedConfigurationFile());
		config = new WaystoneConfig();
		config.reloadLocal(configuration);
		if(configuration.hasChanged()) {
			configuration.save();
		}

		proxy.preInit(event);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		FMLInterModComms.sendMessage("Waila", "register", "net.blay09.mods.waystones.compat.WailaProvider.register");
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		if(instance.config.allowReturnScrolls) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemReturnScroll, 3), "GEG", "PPP", 'G', "nuggetGold", 'E', Items.ENDER_PEARL, 'P', Items.PAPER));
		}

		if(instance.config.allowWarpStone) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemWarpStone), "DED", "EGE", "DED", 'D', "dyePurple", 'E', Items.ENDER_PEARL, 'G', "gemEmerald"));
		}

		if(!config.creativeModeOnly) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockWaystone), " S ", "SWS", "OOO", 'S', Blocks.STONEBRICK, 'W', itemWarpStone, 'O', Blocks.OBSIDIAN));
		}
	}

	public static WaystoneConfig getConfig() {
		return instance.config;
	}

	public void setConfig(WaystoneConfig config) {
		this.config = config;
	}

}
