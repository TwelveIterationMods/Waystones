package net.blay09.mods.waystones.worldgen;

import net.blay09.mods.waystones.WaystoneConfig;

import javax.annotation.Nullable;
import java.util.Random;

public class VillageWaystoneCreationHandler implements VillagerRegistry.IVillageCreationHandler {

	@Override
	public StructureVillagePieces.PieceWeight getVillagePieceWeight(Random random, int size) {
		if (random.nextFloat() > WaystoneConfig.worldGen.villageChance) {
			return new StructureVillagePieces.PieceWeight(getComponentClass(), 0, 0);
		}

		return new StructureVillagePieces.PieceWeight(ComponentVillageWaystone.class, 3, 1);
	}

	@Override
	public Class<? extends StructureVillagePieces.Village> getComponentClass() {
		return ComponentVillageWaystone.class;
	}

	@Override
	@Nullable
	public StructureVillagePieces.Village buildComponent(StructureVillagePieces.PieceWeight villagePiece, StructureVillagePieces.Start startPiece, List<StructureComponent> pieces, Random random, int x, int y, int z, EnumFacing facing, int type) {
		return ComponentVillageWaystone.buildComponent(villagePiece, startPiece, pieces, random, x, y, z, facing, type);
	}

}
