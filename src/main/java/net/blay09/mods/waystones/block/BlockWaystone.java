package net.blay09.mods.waystones.block;

import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.client.render.WaystoneBlockRenderer;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

public class BlockWaystone extends BlockContainer {

	public BlockWaystone() {
		super(Material.rock);

		setBlockName(Waystones.MOD_ID + ":waystone");
		setHardness(5f);
		setResistance(2000f);
		setCreativeTab(CreativeTabs.tabDecorations);
	}

	@Override
	public IIcon getIcon(int side, int metadata) {
		return Blocks.stone.getIcon(side, metadata);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return WaystoneBlockRenderer.RENDER_ID;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return metadata != ForgeDirection.UNKNOWN.ordinal() ? new TileWaystone() : null;
	}

	@Override
	public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, int x, int y, int z) {
		if(Waystones.getConfig().creativeModeOnly && !player.capabilities.isCreativeMode) {
			return -1f;
		}
		return super.getPlayerRelativeBlockHardness(player, world, x, y, z);
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		Block blockBelow = world.getBlock(x, y - 1, z);
		if (blockBelow == this) {
			return false;
		}
		Block blockAbove = world.getBlock(x, y + 2, z);
		return blockAbove != this && super.canPlaceBlockAt(world, x, y, z) && world.getBlock(x, y + 1, z).isReplaceable(world, x, y + 1, z);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLiving, ItemStack itemStack) {
		int orientation = BlockPistonBase.determineOrientation(world, x, y, z, entityLiving);
		world.setBlockMetadataWithNotify(x, y, z, orientation, 1|2);
		world.setBlock(x, y + 1, z, this, ForgeDirection.UNKNOWN.ordinal(), 1|2);
		if(world.isRemote && entityLiving instanceof EntityPlayer && (!Waystones.getConfig().creativeModeOnly || ((EntityPlayer) entityLiving).capabilities.isCreativeMode)) {
			Waystones.proxy.openWaystoneNameEdit((TileWaystone) world.getTileEntity(x, y, z));
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int metadata) {
		TileWaystone tileWaystone = getTileWaystone(world, x, y, z);
		if(tileWaystone != null) {
			WaystoneManager.removeServerWaystone(new WaystoneEntry(tileWaystone));
		}
		super.breakBlock(world, x, y, z, block, metadata);
		if(world.getBlock(x, y + 1, z) == this) {
			world.setBlockToAir(x, y + 1, z);
		} else if(world.getBlock(x, y - 1, z) == this) {
			world.setBlockToAir(x, y - 1, z);
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(player.isSneaking() && (player.capabilities.isCreativeMode || !Waystones.getConfig().creativeModeOnly)) {
			if(world.isRemote) {
				TileWaystone tileWaystone = getTileWaystone(world, x, y, z);
				if(tileWaystone == null) {
					return true;
				}
				Waystones.proxy.openWaystoneNameEdit(tileWaystone);
			}
			return true;
		}
		if(!world.isRemote) {
			TileWaystone tileWaystone = getTileWaystone(world, x, y, z);
			if(tileWaystone == null) {
				return true;
			}
			ChatComponentText nameComponent = new ChatComponentText(tileWaystone.getWaystoneName());
			nameComponent.getChatStyle().setColor(EnumChatFormatting.WHITE);
			ChatComponentTranslation chatComponent = new ChatComponentTranslation("waystones:activatedWaystone", nameComponent);
			chatComponent.getChatStyle().setColor(EnumChatFormatting.YELLOW);
			player.addChatComponentMessage(chatComponent);
			WaystoneManager.activateWaystone(player, tileWaystone);
		} else {
			Waystones.proxy.playSound("random.levelup", 1f);
			for(int i = 0; i < 32; i++) {
				world.spawnParticle("enchantmenttable", x + 0.5 + (world.rand.nextDouble() - 0.5) * 2, y + 3, z + 0.5 + (world.rand.nextDouble() - 0.5) * 2, 0, -5, 0);
				world.spawnParticle("enchantmenttable", x + 0.5 + (world.rand.nextDouble() - 0.5) * 2, y + 4, z + 0.5 + (world.rand.nextDouble() - 0.5) * 2, 0, -5, 0);
			}
		}
		return true;
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random random) {
		if(!WaystoneConfig.disableParticles && random.nextFloat() < 0.75f) {
			TileWaystone tileWaystone = getTileWaystone(world, x, y, z);
			if(tileWaystone == null) {
				return;
			}
			if(WaystoneManager.getKnownWaystone(tileWaystone.getWaystoneName()) != null || WaystoneManager.getServerWaystone(tileWaystone.getWaystoneName()) != null) {
				world.spawnParticle("portal", x + 0.5 + (random.nextDouble() - 0.5) * 1.5, y + 0.5, z + 0.5 + (random.nextDouble() - 0.5) * 1.5, 0, 0, 0);
				world.spawnParticle("enchantmenttable", x + 0.5 + (random.nextDouble() - 0.5) * 1.5, y + 0.5, z + 0.5 + (random.nextDouble() - 0.5) * 1.5, 0, 0, 0);
			}
		}
	}

	public TileWaystone getTileWaystone(World world, int x, int y, int z) {
		TileWaystone tileWaystone = (TileWaystone) world.getTileEntity(x, y, z);
		if(tileWaystone == null) {
			TileEntity tileBelow = world.getTileEntity(x, y - 1, z);
			if(tileBelow instanceof  TileWaystone) {
				return (TileWaystone) tileBelow;
			} else {
				return null;
			}
		}
		return tileWaystone;
	}
}
