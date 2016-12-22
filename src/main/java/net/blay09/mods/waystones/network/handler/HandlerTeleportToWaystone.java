package net.blay09.mods.waystones.network.handler;

import net.blay09.mods.waystones.PlayerWaystoneHelper;
import net.blay09.mods.waystones.WaystoneConfig;
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
						if(!Waystones.getConfig().teleportButtonReturnOnly && player.experienceLevel < xpLevelCost) {
							return;
						}
						if(!Waystones.getConfig().teleportButton || Waystones.getConfig().teleportButtonReturnOnly || !PlayerWaystoneHelper.canFreeWarp(ctx.getServerHandler().playerEntity)) {
							return;
						}
						break;
					case WARP_SCROLL:
						if(heldItem.isEmpty() || heldItem.getItem() != Waystones.itemWarpScroll) {
							return;
						}
						break;
					case WARP_STONE:
						if(Waystones.getConfig().warpStoneXpCost && player.experienceLevel < xpLevelCost) {
							return;
						}
						if(heldItem.isEmpty() || heldItem.getItem() != Waystones.itemWarpStone) {
							return;
						}
						break;
					case WAYSTONE:
						if(player.experienceLevel < xpLevelCost) {
							return;
						}
						WaystoneEntry fromWaystone = message.getFromWaystone();
						if(fromWaystone == null || WaystoneManager.getWaystoneInWorld(fromWaystone) == null) {
							return;
						}
						break;
				}

				if(WaystoneManager.teleportToWaystone(ctx.getServerHandler().playerEntity, message.getWaystone())) {
					boolean shouldCooldown = !(message.getWaystone().isGlobal() && Waystones.getConfig().globalNoCooldown);
					switch(message.getWarpMode()) {
						case INVENTORY_BUTTON:
							if(shouldCooldown) {
								PlayerWaystoneHelper.setLastFreeWarp(ctx.getServerHandler().playerEntity, System.currentTimeMillis());
							}
							player.removeExperienceLevel(xpLevelCost);
							break;
						case WARP_SCROLL:
							heldItem.shrink(1);
							break;
						case WARP_STONE:
							if(Waystones.getConfig().warpStoneXpCost) {
								player.removeExperienceLevel(xpLevelCost);
							}
							if(shouldCooldown) {
								PlayerWaystoneHelper.setLastWarpStoneUse(ctx.getServerHandler().playerEntity, System.currentTimeMillis());
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
