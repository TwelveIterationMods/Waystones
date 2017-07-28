package net.blay09.mods.waystones.block;

import net.blay09.mods.waystones.worldgen.NameGenerator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

public class TileWaystone extends TileEntity {

	private boolean isDummy;
	private String waystoneName = "";
	private UUID owner;
	private boolean isGlobal;

	public TileWaystone() {
	}

	public TileWaystone(boolean isDummy) {
		this.isDummy = isDummy;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tagCompound.setBoolean("IsDummy", isDummy);
		if(!isDummy) {
			tagCompound.setString("WaystoneName", waystoneName);
			if (owner != null) {
				tagCompound.setTag("Owner", NBTUtil.createUUIDTag(owner));
			}
			tagCompound.setBoolean("IsGlobal", isGlobal);
		}
		return tagCompound;
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		isDummy = tagCompound.getBoolean("IsDummy");
		if(!isDummy) {
			waystoneName = tagCompound.getString("WaystoneName");
			if (tagCompound.hasKey("Owner")) {
				owner = NBTUtil.getUUIDFromTag(tagCompound.getCompoundTag("Owner"));
			}
			isGlobal = tagCompound.getBoolean("IsGlobal");
		}
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		generateNameIfNecessary();
		readFromNBT(pkt.getNbtCompound());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		generateNameIfNecessary();
		return writeToNBT(new NBTTagCompound());
	}

	private void generateNameIfNecessary() {
		if(waystoneName.isEmpty()) {
			waystoneName = NameGenerator.getName(world.getBiome(pos), world.rand);
		}
	}

	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
	}

	public String getWaystoneName() {
		return waystoneName;
	}

	public boolean isOwner(EntityPlayer player) {
		return owner == null || player.getGameProfile().getId().equals(owner) || player.capabilities.isCreativeMode;
	}

	public void setWaystoneName(String waystoneName) {
		this.waystoneName = waystoneName;
		IBlockState state = world.getBlockState(pos);
		world.markAndNotifyBlock(pos, world.getChunkFromBlockCoords(pos), state, state, 3);
		markDirty();
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1);
	}

	public void setOwner(EntityPlayer owner) {
		this.owner = owner.getGameProfile().getId();
		markDirty();
	}

	public boolean isGlobal() {
		return isGlobal;
	}

	public void setGlobal(boolean isGlobal) {
		this.isGlobal = isGlobal;
		markDirty();
	}

}
