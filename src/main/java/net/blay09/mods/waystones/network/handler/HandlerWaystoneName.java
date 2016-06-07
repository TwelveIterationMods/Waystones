package net.blay09.mods.waystones.network.handler;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.TileWaystone;
import net.blay09.mods.waystones.network.message.MessageWaystoneName;
import net.blay09.mods.waystones.util.BlockPos;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

public class HandlerWaystoneName implements IMessageHandler<MessageWaystoneName, IMessage> {
	@Override
	public IMessage onMessage(final MessageWaystoneName message, final MessageContext ctx) {
		Waystones.proxy.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				EntityPlayer entityPlayer = ctx.getServerHandler().playerEntity;
				if(Waystones.getConfig().creativeModeOnly && !entityPlayer.capabilities.isCreativeMode) {
					return;
				}
				World world = entityPlayer.getEntityWorld();
				BlockPos pos = message.getPos();
				if(entityPlayer.getDistance(pos.getX(), pos.getY(), pos.getZ()) > 10) {
					return;
				}
				TileEntity tileEntity = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
				if(tileEntity instanceof TileWaystone) {
					if(WaystoneManager.getServerWaystone(message.getName()) != null && !ctx.getServerHandler().playerEntity.capabilities.isCreativeMode) {
						ctx.getServerHandler().playerEntity.addChatComponentMessage(new ChatComponentTranslation("waystones:nameOccupied", message.getName()));
						return;
					}
					WaystoneManager.removeServerWaystone(new WaystoneEntry((TileWaystone) tileEntity));
					((TileWaystone) tileEntity).setWaystoneName(message.getName());
					if(message.isGlobal() && ctx.getServerHandler().playerEntity.capabilities.isCreativeMode) {
						WaystoneManager.addServerWaystone(new WaystoneEntry((TileWaystone) tileEntity));
						for(Object obj : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
							WaystoneManager.sendPlayerWaystones((EntityPlayer) obj);
						}
					}
				}
			}
		});
		return null;
	}
}
