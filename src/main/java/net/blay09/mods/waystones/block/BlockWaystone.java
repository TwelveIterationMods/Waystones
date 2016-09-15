package net.blay09.mods.waystones.block;

import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockWaystone extends BlockContainer {

	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	public static final PropertyBool BASE = PropertyBool.create("base");

	public BlockWaystone() {
		super(Material.ROCK);

		setRegistryName(Waystones.MOD_ID, "waystone");
		setUnlocalizedName(getRegistryName().toString());
		setHardness(5f);
		setResistance(2000f);
		setCreativeTab(CreativeTabs.DECORATIONS);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, BASE);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int meta = state.getValue(FACING).getIndex();
		if (state.getValue(BASE)) {
			meta |= 8;
		}
		return meta;
	}

	@Override
	@SuppressWarnings("deprecation")
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing facing = EnumFacing.getFront(meta & 7);
		if (facing.getAxis() == EnumFacing.Axis.Y) {
			facing = EnumFacing.NORTH;
		}
		boolean isBase = (meta & 8) > 0;
		return getDefaultState().withProperty(FACING, facing).withProperty(BASE, isBase);
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	@Nullable
	public TileEntity createNewTileEntity(World world, int metadata) {
		if((metadata & 8) > 0) {
			return new TileWaystone();
		}
		return null;
	}

	@Override
	@SuppressWarnings("deprecation")
	public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World world, BlockPos pos) {
		if (Waystones.getConfig().creativeModeOnly && !player.capabilities.isCreativeMode) {
			return -1f;
		}
		TileWaystone tileWaystone = (TileWaystone) world.getTileEntity(pos);
		if(tileWaystone != null && WaystoneManager.getServerWaystone(tileWaystone.getWaystoneName()) != null && !player.capabilities.isCreativeMode) {
			return -1f;
		}
		return super.getPlayerRelativeBlockHardness(state, player, world, pos);
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		Block blockBelow = world.getBlockState(pos.down()).getBlock();
		if (blockBelow == this) {
			return false;
		}
		Block blockAbove = world.getBlockState(pos.up(2)).getBlock();
		return blockAbove != this && super.canPlaceBlockAt(world, pos) && world.getBlockState(pos.up()).getBlock().isReplaceable(world, pos.up());
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		EnumFacing facing = BlockPistonBase.getFacingFromEntity(pos, placer);
		if(facing.getAxis() == EnumFacing.Axis.Y) {
			facing = EnumFacing.NORTH;
		}
		world.setBlockState(pos, this.getDefaultState().withProperty(FACING, facing).withProperty(BASE, true));
		world.setBlockState(pos.up(), this.getDefaultState().withProperty(BASE, false));
		if (world.isRemote && placer instanceof EntityPlayer && (!Waystones.getConfig().creativeModeOnly || ((EntityPlayer) placer).capabilities.isCreativeMode)) {
			TileWaystone tileWaystone = (TileWaystone) world.getTileEntity(pos);
			if(tileWaystone != null) {
				Waystones.proxy.openWaystoneNameEdit(tileWaystone);
			}
		}
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileWaystone tileWaystone = getTileWaystone(world, pos);
		if (tileWaystone != null) {
			WaystoneManager.removeServerWaystone(new WaystoneEntry(tileWaystone));
		}
		super.breakBlock(world, pos, state);
		if (world.getBlockState(pos.up()).getBlock() == this) {
			world.setBlockToAir(pos.up());
		} else if (world.getBlockState(pos.down()).getBlock() == this) {
			world.setBlockToAir(pos.down());
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (player.isSneaking() && (player.capabilities.isCreativeMode || !Waystones.getConfig().creativeModeOnly)) {
			if (world.isRemote) {
				TileWaystone tileWaystone = getTileWaystone(world, pos);
				if (tileWaystone == null) {
					return true;
				}
				if(WaystoneManager.getServerWaystone(tileWaystone.getWaystoneName()) != null && !player.capabilities.isCreativeMode) {
					player.addChatComponentMessage(new TextComponentTranslation("waystones:creativeRequired"));
					return true;
				}
				Waystones.proxy.openWaystoneNameEdit(tileWaystone);
			}
			return true;
		}
		if (!world.isRemote) {
			TileWaystone tileWaystone = getTileWaystone(world, pos);
			if (tileWaystone == null) {
				return true;
			}
			TextComponentString nameComponent = new TextComponentString(tileWaystone.getWaystoneName());
			nameComponent.getStyle().setColor(TextFormatting.WHITE);
			TextComponentTranslation chatComponent = new TextComponentTranslation("waystones:activatedWaystone", nameComponent);
			chatComponent.getStyle().setColor(TextFormatting.YELLOW);
			player.addChatComponentMessage(chatComponent);
			WaystoneManager.activateWaystone(player, tileWaystone);
			if (Waystones.getConfig().setSpawnPoint) {
				EnumFacing facing = state.getValue(FACING);
				player.setSpawnChunk(new BlockPos(tileWaystone.getPos().offset(facing)), true, world.provider.getDimension());
			}
		} else {
			Waystones.proxy.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1f);
			for (int i = 0; i < 32; i++) {
				world.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, pos.getX() + 0.5 + (world.rand.nextDouble() - 0.5) * 2, pos.getY() + 3, pos.getZ() + 0.5 + (world.rand.nextDouble() - 0.5) * 2, 0, -5, 0);
				world.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, pos.getX() + 0.5 + (world.rand.nextDouble() - 0.5) * 2, pos.getY() + 4, pos.getZ() + 0.5 + (world.rand.nextDouble() - 0.5) * 2, 0, -5, 0);
			}
		}
		return true;
	}

	@Override
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
		if (!WaystoneConfig.disableParticles && rand.nextFloat() < 0.75f) {
			TileWaystone tileWaystone = getTileWaystone(world, pos);
			if (tileWaystone == null) {
				return;
			}
			if (WaystoneManager.getKnownWaystone(tileWaystone.getWaystoneName()) != null || WaystoneManager.getServerWaystone(tileWaystone.getWaystoneName()) != null) {
				world.spawnParticle(EnumParticleTypes.PORTAL, pos.getX() + 0.5 + (rand.nextDouble() - 0.5) * 1.5, pos.getY() + 0.5, pos.getZ() + 0.5 + (rand.nextDouble() - 0.5) * 1.5, 0, 0, 0);
				world.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, pos.getX() + 0.5 + (rand.nextDouble() - 0.5) * 1.5, pos.getY() + 0.5, pos.getZ() + 0.5 + (rand.nextDouble() - 0.5) * 1.5, 0, 0, 0);
			}
		}
	}

	@Nullable
	public TileWaystone getTileWaystone(World world, BlockPos pos) {
		TileWaystone tileWaystone = (TileWaystone) world.getTileEntity(pos);
		if (tileWaystone == null) {
			TileEntity tileBelow = world.getTileEntity(pos.down());
			if (tileBelow instanceof TileWaystone) {
				return (TileWaystone) tileBelow;
			} else {
				return null;
			}
		}
		return tileWaystone;
	}
}
