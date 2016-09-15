package net.blay09.mods.waystones;

import net.blay09.mods.waystones.block.TileWaystone;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.MessageConfig;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@SuppressWarnings("unused")
public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void addScheduledTask(Runnable runnable) {
		FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(runnable);
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

	public void printChatMessage(int i, ITextComponent chatComponent) {

	}

	public void playSound(SoundEvent soundEvent, float pitch) {

	}
}
