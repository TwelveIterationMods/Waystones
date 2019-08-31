package net.blay09.mods.waystones;

import com.google.common.collect.Lists;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import javax.annotation.Nullable;
import java.util.List;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		GlobalWaystones globalWaystones = GlobalWaystones.get(event.getPlayer().world);
		PlayerWaystoneData waystoneData = PlayerWaystoneData.fromPlayer(event.getPlayer());
		List<WaystoneEntry> validWaystones = Lists.newArrayList();
		for(WaystoneEntry waystone : waystoneData.getWaystones()) {
			if(waystone.isGlobal()) {
				if(globalWaystones.getGlobalWaystone(waystone.getName()) == null) {
					continue;
				}
			}
			validWaystones.add(waystone);
		}
		for(WaystoneEntry waystone : globalWaystones.getGlobalWaystones()) {
			if(!validWaystones.contains(waystone)) {
				validWaystones.add(waystone);
			}
		}
		PlayerWaystoneHelper.store(event.getPlayer(), validWaystones.toArray(new WaystoneEntry[0]), waystoneData.getLastFreeWarp(), waystoneData.getLastWarpStoneUse());

		NetworkHandler.channel.sendTo(new MessageConfig(), (ServerPlayerEntity) event.getPlayer());
		WaystoneManager.sendPlayerWaystones(event.getPlayer());
	}

	@SubscribeEvent
	public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		WaystoneManager.sendPlayerWaystones(event.getPlayer());
	}

	@SubscribeEvent
	public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
		WaystoneManager.sendPlayerWaystones(event.getPlayer());
	}

	public void openWaystoneSelection(PlayerEntity player, WarpMode mode, Hand hand, @Nullable WaystoneEntry fromWaystone) {

	}

	public void openWaystoneSettings(PlayerEntity player, WaystoneEntry waystone, boolean fromSelectionGui) {

	}

	public void playSound(SoundEvent soundEvent, BlockPos pos, float pitch) {

	}

	public boolean isVivecraftInstalled() {
		return false;
	}
}
