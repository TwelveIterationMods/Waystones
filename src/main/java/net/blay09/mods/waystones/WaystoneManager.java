package net.blay09.mods.waystones;

import net.blay09.mods.waystones.block.BlockWaystone;
import net.blay09.mods.waystones.block.TileWaystone;
import net.blay09.mods.waystones.network.message.MessageTeleportEffect;
import net.blay09.mods.waystones.network.message.MessageWaystones;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;
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
				return ((TileWaystone) tileEntity).getParent();
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
		if (dimensionWarp && !WaystoneConfig.general.interDimension && !(waystone.isGlobal() && WaystoneConfig.general.globalInterDimension)) {
			player.sendMessage(new TextComponentTranslation("waystones:noDimensionWarp"));
			return false;
		}
		teleportToPosition(player, targetWorld, targetPos, facing, waystone.getDimensionId());
		return true;
	}

	public static void teleportToPosition(EntityPlayer player, World world, BlockPos pos, EnumFacing facing, int dimensionId) {
		sendTeleportEffect(player.world, new BlockPos(player));
		if(dimensionId != player.getEntityWorld().provider.getDimension()) {
			MinecraftServer server = player.world.getMinecraftServer();
			if(server != null) {
				transferPlayerToDimension((EntityPlayerMP) player, dimensionId, server.getPlayerList());
			}
		}
		player.rotationYaw = getRotationYaw(facing);
		player.setPositionAndUpdate(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
		sendTeleportEffect(player.world, pos);
	}

	/**
	 *  Taken from CoFHCore's EntityHelper (https://github.com/CoFH/CoFHCore/blob/1.12/src/main/java/cofh/core/util/helpers/EntityHelper.java)
	 */
	private static void transferPlayerToDimension(EntityPlayerMP player, int dimension, PlayerList manager) {
		int oldDim = player.dimension;
		WorldServer oldWorld = manager.getServerInstance().getWorld(player.dimension);
		player.dimension = dimension;
		WorldServer newWorld = manager.getServerInstance().getWorld(player.dimension);
		player.connection.sendPacket(new SPacketRespawn(player.dimension, player.world.getDifficulty(), player.world.getWorldInfo().getTerrainType(), player.interactionManager.getGameType()));
		oldWorld.removeEntityDangerously(player);
		if (player.isBeingRidden()) {
			player.removePassengers();
		}
		if (player.isRiding()) {
			player.dismountRidingEntity();
		}
		player.isDead = false;
		transferEntityToWorld(player, oldWorld, newWorld);
		manager.preparePlayer(player, oldWorld);
		player.connection.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
		player.interactionManager.setWorld(newWorld);
		manager.updateTimeAndWeatherForPlayer(player, newWorld);
		manager.syncPlayerInventory(player);

		for (PotionEffect potioneffect : player.getActivePotionEffects()) {
			player.connection.sendPacket(new SPacketEntityEffect(player.getEntityId(), potioneffect));
		}
		FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, oldDim, dimension);
	}

	/**
	 * Taken from CoFHCore's EntityHelper (https://github.com/CoFH/CoFHCore/blob/1.12/src/main/java/cofh/core/util/helpers/EntityHelper.java)
	 */
	private static void transferEntityToWorld(Entity entity, WorldServer oldWorld, WorldServer newWorld) {
		WorldProvider oldWorldProvider = oldWorld.provider;
		WorldProvider newWorldProvider = newWorld.provider;
		double moveFactor = oldWorldProvider.getMovementFactor() / newWorldProvider.getMovementFactor();
		double x = entity.posX * moveFactor;
		double z = entity.posZ * moveFactor;

		oldWorld.profiler.startSection("placing");
		x = MathHelper.clamp(x, -29999872, 29999872);
		z = MathHelper.clamp(z, -29999872, 29999872);
		if (entity.isEntityAlive()) {
			entity.setLocationAndAngles(x, entity.posY, z, entity.rotationYaw, entity.rotationPitch);
			newWorld.spawnEntity(entity);
			newWorld.updateEntityWithOptionalForce(entity, false);
		}
		oldWorld.profiler.endSection();

		entity.setWorld(newWorld);
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
