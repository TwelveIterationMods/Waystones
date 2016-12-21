package net.blay09.mods.waystones.compat;

import com.google.common.base.Function;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.BlockWaystone;
import net.blay09.mods.waystones.block.TileWaystone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class TheOneProbeAddon implements Function<ITheOneProbe, Void> {

	@Nullable
	@Override
	public Void apply(@Nullable ITheOneProbe top) {
		if(top != null) {
			top.registerProvider(new ProbeInfoProvider());
		}
		return null;
	}

	public static class ProbeInfoProvider implements IProbeInfoProvider {

		@Override
		public String getID() {
			return Waystones.MOD_ID;
		}

		@Override
		public void addProbeInfo(ProbeMode mode, IProbeInfo info, EntityPlayer player, World world, IBlockState state, IProbeHitData data) {
			// NOTE no lang support in The One Probe atm...
			if(state.getBlock() instanceof BlockWaystone) {
				TileWaystone tileEntity = tryGetTileEntity(world, data.getPos(), TileWaystone.class);
				if(tileEntity == null) {
					tileEntity = tryGetTileEntity(world, data.getPos().down(), TileWaystone.class);
				}
				if(tileEntity != null) {
					info.text(TextFormatting.DARK_AQUA + tileEntity.getWaystoneName());
				}
			}
		}

	}

	@SuppressWarnings("unchecked")
	@Nullable
	private static <T extends TileEntity> T tryGetTileEntity(World world, BlockPos pos, Class<T> tileClass) {
		TileEntity tileEntity = world.getTileEntity(pos);
		if(tileEntity != null && tileClass.isAssignableFrom(tileEntity.getClass())) {
			return (T) tileEntity;
		}
		return null;
	}

}
