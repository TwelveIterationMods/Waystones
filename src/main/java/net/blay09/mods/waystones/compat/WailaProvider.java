package net.blay09.mods.waystones.compat;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.blay09.mods.waystones.block.BlockWaystone;
import net.blay09.mods.waystones.block.TileWaystone;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import java.util.List;

public class WailaProvider {

	public static void register(IWailaRegistrar registrar) {
		registrar.registerBodyProvider(new WaystoneDataProvider(), BlockWaystone.class);
	}

	private static class WaystoneDataProvider implements IWailaDataProvider {

		@Override
		public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
			return null;
		}

		@Override
		public List<String> getWailaHead(ItemStack itemStack, List<String> list, IWailaDataAccessor accessor, IWailaConfigHandler config) {
			return list;
		}

		@Override
		public List<String> getWailaBody(ItemStack itemStack, List<String> list, IWailaDataAccessor accessor, IWailaConfigHandler config) {
			TileEntity tileEntity = accessor.getTileEntity();
			if(tileEntity == null) {
				tileEntity = accessor.getWorld().getTileEntity(accessor.getPosition().blockX, accessor.getPosition().blockY - 1, accessor.getPosition().blockZ);
			}
			if(tileEntity instanceof TileWaystone) {
				list.add(EnumChatFormatting.DARK_AQUA + ((TileWaystone) tileEntity).getWaystoneName());
			}
			return list;
		}

		@Override
		public List<String> getWailaTail(ItemStack itemStack, List<String> list, IWailaDataAccessor accessor, IWailaConfigHandler config) {
			return list;
		}

		@Override
		public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity tileEntity, NBTTagCompound tagCompound, World world, int x, int y, int z) {
			if(tileEntity != null) {
				tileEntity.writeToNBT(tagCompound);
			}
			return tagCompound;
		}
	}
}
