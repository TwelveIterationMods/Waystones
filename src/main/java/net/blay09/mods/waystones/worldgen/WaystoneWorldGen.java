package net.blay09.mods.waystones.worldgen;

import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.BlockWaystone;
import net.blay09.mods.waystones.block.TileWaystone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class WaystoneWorldGen implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		if(world.provider.getDimension() != 0) {
			return;
		}
		float chance = (float) WaystoneConfig.worldGenChance / 10000f;
		if(random.nextFloat() <= chance) {
			for(int i = 0; i < 5; i++) {
				int blockX = chunkX * 16 + random.nextInt(16);
				int blockZ = chunkZ * 16 + random.nextInt(16);
				BlockPos pos = new BlockPos(blockX, 0, blockZ);
				pos = world.getTopSolidOrLiquidBlock(pos);
				BlockPos posUp = pos.up();
				IBlockState prev = world.getBlockState(pos);
				IBlockState prevUp = world.getBlockState(posUp);
				EnumFacing facing = EnumFacing.values()[2 + random.nextInt(4)];
				if (prev.getBlock() != Blocks.WATER && prev.getBlock().isReplaceable(world, pos) && prevUp.getBlock().isAir(prevUp, world, pos)) {
					world.setBlockState(pos, Waystones.blockWaystone.getDefaultState()
							.withProperty(BlockWaystone.BASE, true)
							.withProperty(BlockWaystone.FACING, facing), 2);
					world.setBlockState(posUp, Waystones.blockWaystone.getDefaultState()
							.withProperty(BlockWaystone.BASE, false)
							.withProperty(BlockWaystone.FACING, facing), 2);
					TileEntity tileEntity = world.getTileEntity(pos);
					if(tileEntity instanceof TileWaystone) {
						((TileWaystone) tileEntity).setWaystoneName(NameGenerator.getName(world.getBiome(pos), random));
					}
					break;
				}
			}
		}
	}

}
