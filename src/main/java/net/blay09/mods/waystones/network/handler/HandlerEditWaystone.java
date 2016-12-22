package net.blay09.mods.waystones.network.handler;

import net.blay09.mods.waystones.GlobalWaystones;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.TileWaystone;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.MessageEditWaystone;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class HandlerEditWaystone implements IMessageHandler<MessageEditWaystone, IMessage> {
	@Override
	@Nullable
	public IMessage onMessage(final MessageEditWaystone message, final MessageContext ctx) {
		NetworkHandler.getThreadListener(ctx).addScheduledTask(new Runnable() {
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
				GlobalWaystones globalWaystones = GlobalWaystones.get(ctx.getServerHandler().playerEntity.world);
				TileEntity tileEntity = world.getTileEntity(pos);
				if(tileEntity instanceof TileWaystone) {
					TileWaystone tileWaystone = (TileWaystone) tileEntity;
					if(globalWaystones.getGlobalWaystone(tileWaystone.getWaystoneName()) != null && !ctx.getServerHandler().playerEntity.capabilities.isCreativeMode) {
						return;
					}
					if(Waystones.getConfig().restrictRenameToOwner && !tileWaystone.isOwner(ctx.getServerHandler().playerEntity)) {
						ctx.getServerHandler().playerEntity.sendMessage(new TextComponentTranslation("waystones:notTheOwner"));
						return;
					}
					if(globalWaystones.getGlobalWaystone(message.getName()) != null && !ctx.getServerHandler().playerEntity.capabilities.isCreativeMode) {
						ctx.getServerHandler().playerEntity.sendMessage(new TextComponentTranslation("waystones:nameOccupied", message.getName()));
						return;
					}
					WaystoneEntry oldWaystone = new WaystoneEntry(tileWaystone);
					globalWaystones.removeGlobalWaystone(oldWaystone);

					tileWaystone.setWaystoneName(message.getName());

					WaystoneEntry newWaystone = new WaystoneEntry(tileWaystone);

					if(message.isGlobal() && ctx.getServerHandler().playerEntity.capabilities.isCreativeMode) {
						tileWaystone.setGlobal(true);
						newWaystone.setGlobal(true);
						globalWaystones.addGlobalWaystone(newWaystone);
						for(Object obj : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
							WaystoneManager.sendPlayerWaystones((EntityPlayer) obj);
						}
					}
				}
			}
		});
		return null;
	}
}
