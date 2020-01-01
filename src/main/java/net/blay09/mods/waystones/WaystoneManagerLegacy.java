package net.blay09.mods.waystones;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.block.WaystoneBlock;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.MessageTeleportEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

@Deprecated
public class WaystoneManagerLegacy {

    public static boolean isDimensionWarpAllowed(IWaystone waystone) {
        return waystone.isGlobal() ? WaystoneConfig.COMMON.globalInterDimension.get() : WaystoneConfig.SERVER.interDimension.get();
    }

    public static boolean teleportToWaystone(PlayerEntity player, IWaystone waystone) {
        MinecraftServer currentServer = ServerLifecycleHooks.getCurrentServer();
        World targetWorld = DimensionManager.getWorld(currentServer, waystone.getDimensionType(), false, true);
        Direction facing = targetWorld.getBlockState(waystone.getPos()).get(WaystoneBlock.FACING);
        BlockPos targetPos = waystone.getPos().offset(facing);
        boolean dimensionWarp = waystone.getDimensionType() != player.getEntityWorld().getDimension().getType();
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
        player.rotationYaw = facing.getHorizontalAngle();
        player.setPositionAndUpdate(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        sendTeleportEffect(player.world, pos);
    }

    public static void sendTeleportEffect(World world, BlockPos pos) {
        MessageTeleportEffect message = new MessageTeleportEffect(pos);
        NetworkHandler.channel.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(pos)), message);
    }

}
