package net.blay09.mods.waystones;

import net.blay09.mods.waystones.block.BlockWaystone;
import net.blay09.mods.waystones.block.TileWaystone;
import net.blay09.mods.waystones.network.message.MessageTeleportEffect;
import net.blay09.mods.waystones.network.message.MessageWaystones;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;

public class WaystoneManager {

	public static void sendPlayerWaystones(EntityPlayer player) {
		if (player instanceof EntityPlayerMP) {
			PlayerWaystoneData waystoneData = PlayerWaystoneData.fromPlayer(player);
			NetworkHandler.channel.sendTo(new MessageWaystones(waystoneData.getWaystones(), waystoneData.getLastFreeWarp(), waystoneData.getLastWarpStoneUse()), (EntityPlayerMP) player);
		}
	}

	public static void addPlayerWaystone(EntityPlayer player, WaystoneEntry waystone) {
		NBTTagCompound tagCompound = PlayerWaystoneHelper.getOrCreateWaystonesTag(player);
		NBTTagList tagList = tagCompound.getTagList(PlayerWaystoneHelper.WAYSTONE_LIST, Constants.NBT.TAG_COMPOUND);
		tagList.appendTag(waystone.writeToNBT());
		tagCompound.setTag(PlayerWaystoneHelper.WAYSTONE_LIST, tagList);
	}

	public static boolean removePlayerWaystone(EntityPlayer player, WaystoneEntry waystone) {
		NBTTagCompound tagCompound = PlayerWaystoneHelper.getWaystonesTag(player);
		NBTTagList tagList = tagCompound.getTagList(PlayerWaystoneHelper.WAYSTONE_LIST, Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound entryCompound = tagList.getCompoundTagAt(i);
			if (WaystoneEntry.read(entryCompound).equals(waystone)) {
				tagList.removeTag(i);
				return true;
			}
		}
		return false;
	}

	public static boolean checkAndUpdateWaystone(EntityPlayer player, WaystoneEntry waystone) {
		NBTTagCompound tagCompound = PlayerWaystoneHelper.getWaystonesTag(player);
		NBTTagList tagList = tagCompound.getTagList(PlayerWaystoneHelper.WAYSTONE_LIST, Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound entryCompound = tagList.getCompoundTagAt(i);
			if (WaystoneEntry.read(entryCompound).equals(waystone)) {
				TileWaystone tileEntity = getWaystoneInWorld(waystone);
				if (tileEntity != null) {
					if (!entryCompound.getString("Name").equals(tileEntity.getWaystoneName())) {
						entryCompound.setString("Name", tileEntity.getWaystoneName());
						sendPlayerWaystones(player);
					}
					return true;
				} else {
					if(waystone.isGlobal()) {
						GlobalWaystones.get(player.world).removeGlobalWaystone(waystone);
					}
					removePlayerWaystone(player, waystone);
					sendPlayerWaystones(player);
				}
				return false;
			}
		}
		return false;
	}

	@Nullable
	public static TileWaystone getWaystoneInWorld(WaystoneEntry waystone) {
		World targetWorld = DimensionManager.getWorld(waystone.getDimensionId());
		if(targetWorld == null) {
			DimensionManager.initDimension(waystone.getDimensionId());
			targetWorld = DimensionManager.getWorld(waystone.getDimensionId());
		}
		if(targetWorld != null) {
			TileEntity tileEntity = targetWorld.getTileEntity(waystone.getPos());
			if (tileEntity instanceof TileWaystone) {
				return (TileWaystone) tileEntity;
			}
		}
		return null;
	}

	public static boolean teleportToWaystone(EntityPlayer player, WaystoneEntry waystone) {
		if(!checkAndUpdateWaystone(player, waystone)) {
			TextComponentTranslation chatComponent = new TextComponentTranslation("waystones:waystoneBroken");
			chatComponent.getStyle().setColor(TextFormatting.RED);
			player.sendMessage(chatComponent);
			return false;
		}
		World targetWorld = DimensionManager.getWorld(waystone.getDimensionId());
		EnumFacing facing = targetWorld.getBlockState(waystone.getPos()).getValue(BlockWaystone.FACING);
		BlockPos targetPos = waystone.getPos().offset(facing);
		boolean dimensionWarp = waystone.getDimensionId() != player.getEntityWorld().provider.getDimension();
		if (dimensionWarp && !Waystones.getConfig().interDimension && !(waystone.isGlobal() && Waystones.getConfig().globalInterDimension)) {
			player.sendMessage(new TextComponentTranslation("waystones:noDimensionWarp"));
			return false;
		}
		sendTeleportEffect(player.world, new BlockPos(player));
//		player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 20, 3));
		if(dimensionWarp) {
			MinecraftServer server = player.world.getMinecraftServer();
			if(server != null) {
				server.getPlayerList().transferPlayerToDimension((EntityPlayerMP) player, waystone.getDimensionId(), new TeleporterWaystone((WorldServer) player.world));
			}
		}
		player.rotationYaw = getRotationYaw(facing);
		player.setPositionAndUpdate(targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5);
		sendTeleportEffect(player.world, targetPos);
		return true;
	}

	public static void sendTeleportEffect(World world, BlockPos pos) {
		NetworkHandler.channel.sendToAllAround(new MessageTeleportEffect(pos), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
	}

	public static float getRotationYaw(EnumFacing facing) {
		switch(facing) {
			case NORTH:
				return 180f;
			case SOUTH:
				return 0f;
			case WEST:
				return 90f;
			case EAST:
				return -90f;
		}
		return 0f;
	}

}
