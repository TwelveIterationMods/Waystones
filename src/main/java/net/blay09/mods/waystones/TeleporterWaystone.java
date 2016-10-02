package net.blay09.mods.waystones;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class TeleporterWaystone extends Teleporter {
	public TeleporterWaystone(WorldServer world) {
		super(world);
	}

	@Override
	public void placeInPortal(Entity entity, float rotationYaw) {
		// cool cool
	}
}
