package net.blay09.mods.waystones;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.blay09.mods.waystones.block.TileWaystone;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.MessageConfig;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(this);
	}

	public void addScheduledTask(Runnable runnable) {
		runnable.run();
	}

	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		NetworkHandler.channel.sendTo(new MessageConfig(Waystones.getConfig()), (EntityPlayerMP) event.player);
		WaystoneManager.sendPlayerWaystones(event.player);
	}

	@SubscribeEvent
	public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		WaystoneManager.sendPlayerWaystones(event.player);
	}

	public void openWaystoneNameEdit(TileWaystone tileEntity) {

	}

	public void openWaystoneSelection(boolean isFree) {

	}

	public void printChatMessage(int i, IChatComponent chatComponent) {

	}

	public void playSound(String soundName, float pitch) {

	}
}
