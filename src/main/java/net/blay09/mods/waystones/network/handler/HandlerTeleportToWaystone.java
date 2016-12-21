package net.blay09.mods.waystones.network.handler;

import net.blay09.mods.waystones.PlayerWaystoneData;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.TileWaystone;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.MessageTeleportToWaystone;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class HandlerTeleportToWaystone implements IMessageHandler<MessageTeleportToWaystone, IMessage> {
	@Override
	@Nullable
	public IMessage onMessage(final MessageTeleportToWaystone message, final MessageContext ctx) {
		NetworkHandler.getThreadListener(ctx).addScheduledTask(new Runnable() {
			@Override
			public void run() {
				ItemStack heldItem = ctx.getServerHandler().playerEntity.getHeldItem(message.getHand());
				switch(message.getWarpMode()) {
					case INVENTORY_BUTTON:
						if(!Waystones.getConfig().teleportButton || Waystones.getConfig().teleportButtonReturnOnly || !PlayerWaystoneData.canFreeWarp(ctx.getServerHandler().playerEntity)) {
							return;
						}
						break;
					case WARP_SCROLL:
						if(heldItem.isEmpty() || heldItem.getItem() != Waystones.itemWarpScroll) {
							return;
						}
						break;
					case WARP_STONE:
						if(heldItem.isEmpty() || heldItem.getItem() != Waystones.itemWarpStone) {
							return;
						}
					case WAYSTONE:
						WaystoneEntry fromWaystone = message.getFromWaystone();
						if(fromWaystone == null || WaystoneManager.getWaystoneInWorld(fromWaystone) == null) {
							return;
						}
						break;
				}

				if(WaystoneManager.teleportToWaystone(ctx.getServerHandler().playerEntity, message.getWaystone())) {
					boolean shouldCooldown = WaystoneManager.getServerWaystone(message.getWaystone().getName()) == null || !Waystones.getConfig().globalNoCooldown;
					switch(message.getWarpMode()) {
						case INVENTORY_BUTTON:
							if(shouldCooldown) {
								PlayerWaystoneData.setLastFreeWarp(ctx.getServerHandler().playerEntity, System.currentTimeMillis());
							}
							break;
						case WARP_SCROLL:
							heldItem.shrink(1);
							break;
						case WARP_STONE:
							if(shouldCooldown) {
								PlayerWaystoneData.setLastWarpStoneUse(ctx.getServerHandler().playerEntity, System.currentTimeMillis());
							}
							break;
					}
				}

				WaystoneManager.sendPlayerWaystones(ctx.getServerHandler().playerEntity);
			}
		});
		return null;
	}
}
