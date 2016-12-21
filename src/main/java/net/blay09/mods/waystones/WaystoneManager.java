package net.blay09.mods.waystones;

import com.google.common.collect.Maps;
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
import java.util.Collection;
import java.util.Map;

public class WaystoneManager {

	private static final Map<String, WaystoneEntry> serverWaystones = Maps.newHashMap();
	private static final Map<String, WaystoneEntry> knownWaystones = Maps.newHashMap();

	public static void activateWaystone(EntityPlayer player, TileWaystone waystone) {
		WaystoneEntry serverWaystone = getServerWaystone(waystone.getWaystoneName());
		if(serverWaystone != null) {
			PlayerWaystoneData.setLastServerWaystone(player, serverWaystone);
			sendPlayerWaystones(player);
			return;
		}
		PlayerWaystoneData.resetLastServerWaystone(player);
		removePlayerWaystone(player, new WaystoneEntry(waystone));
		addPlayerWaystone(player, waystone);
		sendPlayerWaystones(player);
	}

	public static void sendPlayerWaystones(EntityPlayer player) {
		if (player instanceof EntityPlayerMP) {
			PlayerWaystoneData waystoneData = PlayerWaystoneData.fromPlayer(player);
			NetworkHandler.channel.sendTo(new MessageWaystones(waystoneData.getWaystones(), getServerWaystones().toArray(new WaystoneEntry[getServerWaystones().size()]), waystoneData.getLastServerWaystoneName(), waystoneData.getLastFreeWarp(), waystoneData.getLastWarpStoneUse()), (EntityPlayerMP) player);
		}
	}

	public static void addPlayerWaystone(EntityPlayer player, TileWaystone waystone) {
		NBTTagCompound tagCompound = PlayerWaystoneData.getOrCreateWaystonesTag(player);
		NBTTagList tagList = tagCompound.getTagList(PlayerWaystoneData.WAYSTONE_LIST, Constants.NBT.TAG_COMPOUND);
		tagList.appendTag(new WaystoneEntry(waystone).writeToNBT());
		tagCompound.setTag(PlayerWaystoneData.WAYSTONE_LIST, tagList);
	}

	public static boolean removePlayerWaystone(EntityPlayer player, WaystoneEntry waystone) {
		NBTTagCompound tagCompound = PlayerWaystoneData.getWaystonesTag(player);
		NBTTagList tagList = tagCompound.getTagList(PlayerWaystoneData.WAYSTONE_LIST, Constants.NBT.TAG_COMPOUND);
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
		WaystoneEntry serverEntry = getServerWaystone(waystone.getName());
		if(serverEntry != null) {
			if(getWaystoneInWorld(serverEntry) == null) {
				removeServerWaystone(serverEntry);
				return false;
			}
			if(removePlayerWaystone(player, waystone)) {
				sendPlayerWaystones(player);
			}
			return true;
		}
		NBTTagCompound tagCompound = PlayerWaystoneData.getWaystonesTag(player);
		NBTTagList tagList = tagCompound.getTagList(PlayerWaystoneData.WAYSTONE_LIST, Constants.NBT.TAG_COMPOUND);
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
		WaystoneEntry serverEntry = getServerWaystone(waystone.getName());
		World targetWorld = DimensionManager.getWorld(waystone.getDimensionId());
		EnumFacing facing = targetWorld.getBlockState(waystone.getPos()).getValue(BlockWaystone.FACING);
		BlockPos targetPos = waystone.getPos().offset(facing);
		boolean dimensionWarp = waystone.getDimensionId() != player.getEntityWorld().provider.getDimension();
		if (dimensionWarp && !Waystones.getConfig().interDimension && !(serverEntry == null || !Waystones.getConfig().globalInterDimension)) {
			player.sendMessage(new TextComponentTranslation("waystones:noDimensionWarp"));
			return false;
		}
		sendTeleportEffect(player.world, new BlockPos(player));
		player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 20, 3));
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

	public static void addServerWaystone(WaystoneEntry entry) {
		serverWaystones.put(entry.getName(), entry);
		WaystoneConfig.storeServerWaystones(Waystones.configuration, serverWaystones.values());
	}

	public static void removeServerWaystone(WaystoneEntry entry) {
		serverWaystones.remove(entry.getName());
		WaystoneConfig.storeServerWaystones(Waystones.configuration, serverWaystones.values());
	}

	public static void setServerWaystones(WaystoneEntry[] entries) {
		serverWaystones.clear();
		for(WaystoneEntry entry : entries) {
			serverWaystones.put(entry.getName(), entry);
		}
	}

	public static void setKnownWaystones(WaystoneEntry[] entries) {
		knownWaystones.clear();
		for(WaystoneEntry entry : entries) {
			knownWaystones.put(entry.getName(), entry);
		}
	}

	@Nullable
	public static WaystoneEntry getKnownWaystone(String name) {
		return knownWaystones.get(name);
	}

	public static Collection<WaystoneEntry> getServerWaystones() {
		return serverWaystones.values();
	}

	@Nullable
	public static WaystoneEntry getServerWaystone(String name) {
		return serverWaystones.get(name);
	}
}
