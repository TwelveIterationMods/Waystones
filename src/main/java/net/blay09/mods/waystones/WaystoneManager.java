package net.blay09.mods.waystones;

import net.blay09.mods.waystones.block.WaystoneBlock;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.MessageTeleportEffect;
import net.blay09.mods.waystones.network.message.MessageWaystones;
import net.blay09.mods.waystones.tileentity.WaystoneTileEntity;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nullable;

public class WaystoneManager {

    public static void sendPlayerWaystones(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity) {
            PlayerWaystoneData waystoneData = PlayerWaystoneData.fromPlayer(player);
            NetworkHandler.sendTo(new MessageWaystones(waystoneData.getWaystones(), waystoneData.getLastFreeWarp(), waystoneData.getLastWarpStoneUse()), player);
        }
    }

    public static void addPlayerWaystone(PlayerEntity player, WaystoneEntry waystone) {
        CompoundNBT tagCompound = PlayerWaystoneHelper.getOrCreateWaystonesTag(player);
        ListNBT tagList = tagCompound.getList(PlayerWaystoneHelper.WAYSTONE_LIST, Constants.NBT.TAG_COMPOUND);
        tagList.add(waystone.writeToNBT());
        tagCompound.put(PlayerWaystoneHelper.WAYSTONE_LIST, tagList);
    }

    public static boolean removePlayerWaystone(PlayerEntity player, WaystoneEntry waystone) {
        CompoundNBT tagCompound = PlayerWaystoneHelper.getWaystonesTag(player);
        ListNBT tagList = tagCompound.getList(PlayerWaystoneHelper.WAYSTONE_LIST, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tagList.size(); i++) {
            CompoundNBT entryCompound = tagList.getCompound(i);
            if (WaystoneEntry.read(entryCompound).equals(waystone)) {
                tagList.remove(i);
                return true;
            }
        }

        return false;
    }

    public static boolean checkAndUpdateWaystone(PlayerEntity player, WaystoneEntry waystone) {
        CompoundNBT tagCompound = PlayerWaystoneHelper.getWaystonesTag(player);
        ListNBT tagList = tagCompound.getList(PlayerWaystoneHelper.WAYSTONE_LIST, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tagList.size(); i++) {
            CompoundNBT entryCompound = tagList.getCompound(i);
            if (WaystoneEntry.read(entryCompound).equals(waystone)) {
                WaystoneTileEntity tileEntity = getWaystoneInWorld(waystone);
                if (tileEntity != null) {
                    if (!entryCompound.getString("Name").equals(tileEntity.getWaystoneName())) {
                        entryCompound.putString("Name", tileEntity.getWaystoneName());
                        sendPlayerWaystones(player);
                    }
                    return true;
                } else {
                    if (waystone.isGlobal()) {
                        GlobalWaystones.get(player.world).removeGlobalWaystone(waystone);
                    }
                    removePlayerWaystone(player, waystone);
                    sendPlayerWaystones(player);
                }
                return false;
            }
        }
        return false;
    }

    @Nullable
    public static WaystoneTileEntity getWaystoneInWorld(WaystoneEntry waystone) {
        MinecraftServer currentServer = ServerLifecycleHooks.getCurrentServer();
        World targetWorld = DimensionManager.getWorld(currentServer, waystone.getDimension(), false, true);
        if (targetWorld != null) {
            TileEntity tileEntity = targetWorld.getTileEntity(waystone.getPos());
            if (tileEntity instanceof WaystoneTileEntity) {
                return ((WaystoneTileEntity) tileEntity).getParent();
            }
        }

        return null;
    }

    public static boolean isDimensionWarpAllowed(WaystoneEntry waystone) {
        return waystone.isGlobal() ? WaystoneConfig.COMMON.globalInterDimension.get() : WaystoneConfig.SERVER.interDimension.get();
    }

    public static boolean teleportToWaystone(PlayerEntity player, WaystoneEntry waystone) {
        if (!checkAndUpdateWaystone(player, waystone)) {
            TranslationTextComponent chatComponent = new TranslationTextComponent("waystones:waystoneBroken");
            chatComponent.getStyle().setColor(TextFormatting.RED);
            player.sendMessage(chatComponent);
            return false;
        }

        MinecraftServer currentServer = ServerLifecycleHooks.getCurrentServer();
        World targetWorld = DimensionManager.getWorld(currentServer, waystone.getDimension(), false, true);
        Direction facing = targetWorld.getBlockState(waystone.getPos()).get(WaystoneBlock.FACING);
        BlockPos targetPos = waystone.getPos().offset(facing);
        boolean dimensionWarp = waystone.getDimension() != player.getEntityWorld().getDimension().getType();
        if (dimensionWarp && !isDimensionWarpAllowed(waystone)) {
            player.sendMessage(new TranslationTextComponent("waystones:noDimensionWarp"));
            return false;
        }

        teleportToPosition(player, targetWorld, targetPos, facing);
        return true;
    }

    public static void teleportToPosition(PlayerEntity player, World world, BlockPos pos, Direction facing) {
        sendTeleportEffect(player.world, new BlockPos(player));
        if (world.getDimension() != player.getEntityWorld().getDimension()) {
            MinecraftServer server = player.world.getServer();
            if (server != null) {
                // TODO transferPlayerToDimension((ServerPlayerEntity) player, dimension, server.getPlayerList());
            }
        } else {
            if (player.isBeingRidden()) {
                player.removePassengers();
            }
            if (player.isPassenger()) {
                player.stopRiding();
            }
        }
        player.rotationYaw = getRotationYaw(facing);
        player.setPositionAndUpdate(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        sendTeleportEffect(player.world, pos);
    }

    public static void sendTeleportEffect(World world, BlockPos pos) {
        MessageTeleportEffect message = new MessageTeleportEffect(pos);
        NetworkHandler.channel.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(pos)), message);
    }

    public static float getRotationYaw(Direction facing) {
        switch (facing) {
            case NORTH:
                return 180f;
            case SOUTH:
                return 0f;
            case WEST:
                return 90f;
            case EAST:
                return -90f;
        }
        return 0f;
    }

}
