package net.blay09.mods.waystones.network.handler;

import net.blay09.mods.waystones.PlayerWaystoneData;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.MessageTeleportToWaystone;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
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
				EntityPlayer player = ctx.getServerHandler().playerEntity;
				int dist = (int) Math.sqrt(player.getDistanceSqToCenter(message.getWaystone().getPos()));
				int xpLevelCost = Waystones.getConfig().blocksPerXPLevel > 0 ? MathHelper.clamp(dist / Waystones.getConfig().blocksPerXPLevel, 0, 3) : 0;
				ItemStack heldItem = player.getHeldItem(message.getHand());
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
						if(player.experienceLevel < xpLevelCost) {
							return;
						}
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
							player.removeExperienceLevel(xpLevelCost);
							if(shouldCooldown) {
								PlayerWaystoneData.setLastWarpStoneUse(ctx.getServerHandler().playerEntity, System.currentTimeMillis());
							}
							break;
						case WAYSTONE:
							player.removeExperienceLevel(xpLevelCost);
							break;
					}
				}

				WaystoneManager.sendPlayerWaystones(ctx.getServerHandler().playerEntity);
			}
		});
		return null;
	}
}
