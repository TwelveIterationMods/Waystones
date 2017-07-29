package net.blay09.mods.waystones.worldgen;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.BlockWaystone;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ComponentVillageWaystone extends StructureVillagePieces.Village {
	private static final ResourceLocation VILLAGE_WAYSTONE_ID = new ResourceLocation(Waystones.MOD_ID, "village_waystone");
	private static final ResourceLocation DESERT_VILLAGE_WAYSTONE_ID = new ResourceLocation(Waystones.MOD_ID, "desert_village_waystone");

	public ComponentVillageWaystone() {
	}

	public ComponentVillageWaystone(StructureVillagePieces.Start start, int type, StructureBoundingBox boundingBox, EnumFacing facing) {
		super(start, type);
		this.boundingBox = boundingBox;
		setCoordBaseMode(facing);
	}

	@Override
	public boolean addComponentParts(World world, Random random, StructureBoundingBox boundingBox) {
		if (averageGroundLvl < 0) {
			averageGroundLvl = getAverageGroundLevel(world, boundingBox);
			if (averageGroundLvl < 0) {
				return true;
			}

			boundingBox.offset(0, averageGroundLvl - boundingBox.minY, 0);
		}
		BlockPos pos = new BlockPos(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
		TemplateManager templateManager = world.getSaveHandler().getStructureTemplateManager();
		PlacementSettings settings = (new PlacementSettings()).setReplacedBlock(Blocks.STRUCTURE_VOID).setBoundingBox(boundingBox);
		Template template = templateManager.getTemplate(world.getMinecraftServer(), componentType == 1 ? DESERT_VILLAGE_WAYSTONE_ID : VILLAGE_WAYSTONE_ID);
		template.addBlocksToWorldChunk(world, pos, settings);
		Map<BlockPos, String> dataBlocks = template.getDataBlocks(pos, settings);
		System.out.println(pos);
		for (Map.Entry<BlockPos, String> entry : dataBlocks.entrySet()) {
			if ("Waystone".equals(entry.getValue())) {
				world.setBlockState(entry.getKey(), Waystones.blockWaystone.getDefaultState().withProperty(BlockWaystone.BASE, true), 3);
				world.setBlockState(entry.getKey().up(), Waystones.blockWaystone.getDefaultState().withProperty(BlockWaystone.BASE, false), 3);
			}
		}
		return true;
	}

	@Nullable
	public static StructureVillagePieces.Village buildComponent(StructureVillagePieces.PieceWeight villagePiece, StructureVillagePieces.Start startPiece, List<StructureComponent> pieces, Random random, int x, int y, int z, EnumFacing facing, int type) {
		StructureBoundingBox boundingBox = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 5, 6, 5, facing);
		if (canVillageGoDeeper(boundingBox) && findIntersecting(pieces, boundingBox) == null) {
			return new ComponentVillageWaystone(startPiece, type, boundingBox, facing);
		}
		return null;
	}
}
