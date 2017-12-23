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
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class HandlerTeleportToWaystone implements IMessageHandler<MessageTeleportToWaystone, IMessage> {
	@Override
	@Nullable
	public IMessage onMessage(final MessageTeleportToWaystone message, final MessageContext ctx) {
		NetworkHandler.getThreadListener(ctx).addScheduledTask(() -> {
			EntityPlayer player = ctx.getServerHandler().player;
			int dist = (int) Math.sqrt(player.getDistanceSqToCenter(message.getWaystone().getPos()));
			int xpLevelCost = WaystoneConfig.general.blocksPerXPLevel > 0 ? MathHelper.clamp(dist / WaystoneConfig.general.blocksPerXPLevel, 0, WaystoneConfig.general.maximumXpCost) : 0;
			ItemStack heldItem = player.getHeldItem(message.getHand());
			switch (message.getWarpMode()) {
				case INVENTORY_BUTTON:
					if (!WaystoneConfig.general.teleportButtonReturnOnly && player.experienceLevel < xpLevelCost) {
						return;
					}
					if (!WaystoneConfig.general.teleportButton || WaystoneConfig.general.teleportButtonReturnOnly || !PlayerWaystoneHelper.canFreeWarp(ctx.getServerHandler().player)) {
						return;
					}
					break;
				case WARP_SCROLL:
					if (heldItem.isEmpty() || heldItem.getItem() != Waystones.itemWarpScroll) {
						return;
					}
					break;
				case WARP_STONE:
					if (WaystoneConfig.general.warpStoneXpCost && player.experienceLevel < xpLevelCost) {
						return;
					}
					if (heldItem.isEmpty() || heldItem.getItem() != Waystones.itemWarpStone) {
						return;
					}
					if (!PlayerWaystoneHelper.canUseWarpStone(player)) {
						return;
					}
					break;
				case WAYSTONE:
					if (player.experienceLevel < xpLevelCost) {
						return;
					}
					WaystoneEntry fromWaystone = message.getFromWaystone();
					if (fromWaystone == null || WaystoneManager.getWaystoneInWorld(fromWaystone) == null) {
						return;
					}
					break;
			}

			if (WaystoneManager.teleportToWaystone(ctx.getServerHandler().player, message.getWaystone())) {
				boolean shouldCooldown = !(message.getWaystone().isGlobal() && WaystoneConfig.general.globalNoCooldown);
				switch (message.getWarpMode()) {
					case INVENTORY_BUTTON:
						if (shouldCooldown) {
							PlayerWaystoneHelper.setLastFreeWarp(ctx.getServerHandler().player, System.currentTimeMillis());
						}
						player.addExperienceLevel(-xpLevelCost);
						break;
					case WARP_SCROLL:
						heldItem.shrink(1);
						break;
					case WARP_STONE:
						if (WaystoneConfig.general.warpStoneXpCost) {
							player.addExperienceLevel(-xpLevelCost);
						}

						if (shouldCooldown) {
							PlayerWaystoneHelper.setLastWarpStoneUse(ctx.getServerHandler().player, System.currentTimeMillis());
						}
						break;
					case WAYSTONE:
						player.addExperienceLevel(-xpLevelCost);
						break;
				}
			}

			WaystoneManager.sendPlayerWaystones(ctx.getServerHandler().player);
		});
		return null;
	}
}
