package net.blay09.mods.waystones;

import io.netty.buffer.ByteBuf;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.config.Configuration;

import java.util.Collection;

public class WaystoneConfig {

	public static int teleportButtonX;
	public static int teleportButtonY;
	public static boolean disableParticles;
	public static boolean disableTextGlow;

	public boolean teleportButton;
	public int teleportButtonCooldown;
	public boolean teleportButtonReturnOnly;

	public boolean allowReturnScrolls;
	public boolean allowWarpScrolls;
	public boolean allowWarpStone;

	public int blocksPerXPLevel;
	public int warpStoneCooldown;

	public boolean interDimension;

	public boolean restrictRenameToOwner;
	public boolean creativeModeOnly;
	public boolean setSpawnPoint;

	public boolean globalNoCooldown;
	public boolean globalInterDimension;

	public static float soundVolume = 0.5f;

	public void reloadLocal(Configuration config) {
		teleportButton = config.getBoolean("Teleport Button in GUI", "general", false, "Should there be a button in the inventory to access the waystone menu?");
		teleportButtonCooldown = config.getInt("Teleport Button Cooldown", "general", 300, 0, 86400, "The cooldown between usages of the teleport button in seconds.");
		teleportButtonReturnOnly = config.getBoolean("Teleport Button Return Only", "general", true, "If true, the teleport button will only let you return to the last activated waystone, instead of allowing to choose.");

		allowReturnScrolls = config.getBoolean("Allow Return Scrolls", "general", true, "If true, return scrolls will be craftable.");
		allowWarpScrolls = config.getBoolean("Allow Warp Scrolls", "general", true, "If true, warp scrolls will be craftable.");
		allowWarpStone = config.getBoolean("Allow Warp Stone", "general", true, "If true, the warp stone will be craftable.");

		teleportButtonX = config.getInt("Teleport Button GUI X", "client", 58, -100, 250, "The x position of the warp button in the inventory.");
		teleportButtonY = config.getInt("Teleport Button GUI Y", "client", 60, -100, 250, "The y position of the warp button in the inventory.");
		disableTextGlow = config.getBoolean("Disable Text Glow", "client", false, "If true, the text overlay on waystones will no longer always render at full brightness.");
		disableParticles = config.getBoolean("Disable Particles", "client", false, "If true, activated waystones will not emit particles.");

		warpStoneCooldown = config.getInt("Warp Stone Cooldown", "general", 300, 0, 86400, "The cooldown between usages of the warp stone in seconds.");
		blocksPerXPLevel = config.getInt("Blocks per XP Level", "general", 0, 0, 2000, "The amount of blocks per xp level requirement. Set to 0 to disable xp requirement.");

		setSpawnPoint = config.getBoolean("Set Spawnpoint on Activation", "general", false, "If true, the player's spawnpoint will be set to the last activated waystone.");
		interDimension = config.getBoolean("Interdimensional Teleport", "general", true, "If true, all waystones work inter-dimensionally.");

		restrictRenameToOwner = config.getBoolean("Restrict Rename to Owner", "general", false, "If true, only the owner of a waystone can rename it.");
		creativeModeOnly = config.getBoolean("Creative Mode Only", "general", false, "If true, waystones can only be placed in creative mode.");

		globalNoCooldown = config.getBoolean("No Cooldown on Global Waystones", "general", true, "If true, waystones marked as global have no cooldown.");
		globalInterDimension = config.getBoolean("Interdimensional Teleport on Global Waystones", "general", true, "If true, waystones marked as global work inter-dimensionally.");

		soundVolume = config.getFloat("Sound Volume", "client", 0.5f, 0f, 1f, "The volume of the sound played when teleporting.");

		String[] serverWaystoneData = config.getStringList("Server Waystones", "generated", new String[0], "This option is automatically populated by the server when using the Server Hub Mode. Do not change.");
		WaystoneEntry[] serverWaystones = new WaystoneEntry[serverWaystoneData.length];
		for(int i = 0; i < serverWaystones.length; i++) {
			String[] split = serverWaystoneData[i].split("\u00a7");
			if(split.length < 3) {
				serverWaystones[i] = new WaystoneEntry("Invalid Waystone", 0, new BlockPos(0, 0, 0));
				serverWaystones[i].setGlobal(true);
				continue;
			}
			try {
				int dimensionId = Integer.parseInt(split[1]);
				String[] pos = split[2].split(",");
				serverWaystones[i] = new WaystoneEntry(split[0], dimensionId, new BlockPos(Integer.parseInt(pos[0]), Integer.parseInt(pos[1]), Integer.parseInt(pos[2])));
				serverWaystones[i].setGlobal(true);
			} catch (NumberFormatException e) {
				serverWaystones[i] = new WaystoneEntry("Invalid Waystone", 0, new BlockPos(0, 0, 0));
				serverWaystones[i].setGlobal(true);
			}
		}
		WaystoneManager.setServerWaystones(serverWaystones);
	}

	public static void storeServerWaystones(Configuration config, Collection<WaystoneEntry> entries) {
		String[] serverWaystones = new String[entries.size()];
		int i = 0;
		for(WaystoneEntry entry : entries) {
			serverWaystones[i] = entry.getName() + "\u00a7" + entry.getDimensionId() + "\u00a7" + entry.getPos().getX() + "," + entry.getPos().getY() + "," + entry.getPos().getZ();
			i++;
		}
		config.get("generated", "Server Waystones", "This option is automatically populated by the server when using the Server Hub Mode. Do not change.").set(serverWaystones);
		if(config.hasChanged()) {
			config.save();
		}
	}

	public static WaystoneConfig read(ByteBuf buf) {
		WaystoneConfig config = new WaystoneConfig();
		config.teleportButton = buf.readBoolean();
		config.teleportButtonCooldown = buf.readInt();
		config.teleportButtonReturnOnly = buf.readBoolean();
		config.warpStoneCooldown = buf.readInt();
		config.interDimension = buf.readBoolean();
		config.creativeModeOnly = buf.readBoolean();
		config.setSpawnPoint = buf.readBoolean();
		config.restrictRenameToOwner = buf.readBoolean();
		config.blocksPerXPLevel = buf.readInt();
		return config;
	}

	public void write(ByteBuf buf) {
		buf.writeBoolean(teleportButton);
		buf.writeInt(teleportButtonCooldown);
		buf.writeBoolean(teleportButtonReturnOnly);
		buf.writeInt(warpStoneCooldown);
		buf.writeBoolean(interDimension);
		buf.writeBoolean(creativeModeOnly);
		buf.writeBoolean(setSpawnPoint);
		buf.writeBoolean(restrictRenameToOwner);
		buf.writeInt(blocksPerXPLevel);
	}
}
