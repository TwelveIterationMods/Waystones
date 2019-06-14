package net.blay09.mods.waystones;

import net.blay09.mods.waystones.block.BlockWaystone;
import net.blay09.mods.waystones.block.TileWaystone;
import net.blay09.mods.waystones.network.message.MessageTeleportEffect;
import net.blay09.mods.waystones.network.message.MessageWaystones;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.network.play.server.SRespawnPacket;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;

public class WaystoneManager {

    public static void sendPlayerWaystones(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity) {
            PlayerWaystoneData waystoneData = PlayerWaystoneData.fromPlayer(player);
            NetworkHandler.channel.sendTo(new MessageWaystones(waystoneData.getWaystones(), waystoneData.getLastFreeWarp(), waystoneData.getLastWarpStoneUse()), (ServerPlayerEntity) player);
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
                TileWaystone tileEntity = getWaystoneInWorld(waystone);
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
    public static TileWaystone getWaystoneInWorld(WaystoneEntry waystone) {
        World targetWorld = DimensionManager.getWorld(waystone.getDimensionId());
        if (targetWorld == null) {
            DimensionManager.initDimension(waystone.getDimensionId());
            targetWorld = DimensionManager.getWorld(waystone.getDimensionId());
        }
        if (targetWorld != null) {
            TileEntity tileEntity = targetWorld.getTileEntity(waystone.getPos());
            if (tileEntity instanceof TileWaystone) {
                return ((TileWaystone) tileEntity).getParent();
            }
        }
        return null;
    }

    public static boolean isDimensionWarpAllowed(WaystoneEntry waystone) {
        return waystone.isGlobal() ? WaystoneConfig.general.globalInterDimension : WaystoneConfig.general.interDimension;
    }

    public static boolean teleportToWaystone(PlayerEntity player, WaystoneEntry waystone) {
        if (!checkAndUpdateWaystone(player, waystone)) {
            TranslationTextComponent chatComponent = new TranslationTextComponent("waystones:waystoneBroken");
            chatComponent.getStyle().setColor(TextFormatting.RED);
            player.sendMessage(chatComponent);
            return false;
        }

        World targetWorld = DimensionManager.getWorld(waystone.getDimensionId());
        Direction facing = targetWorld.getBlockState(waystone.getPos()).getValue(BlockWaystone.FACING);
        BlockPos targetPos = waystone.getPos().offset(facing);
        boolean dimensionWarp = waystone.getDimensionId() != player.getEntityWorld().provider.getDimension();
        if (dimensionWarp && !isDimensionWarpAllowed(waystone)) {
            player.sendMessage(new TranslationTextComponent("waystones:noDimensionWarp"));
            return false;
        }

        teleportToPosition(player, targetWorld, targetPos, facing, waystone.getDimensionId());
        return true;
    }

    public static void teleportToPosition(PlayerEntity player, World world, BlockPos pos, Direction facing, int dimensionId) {
        sendTeleportEffect(player.world, new BlockPos(player));
        if (dimensionId != player.getEntityWorld().provider.getDimension()) {
            MinecraftServer server = player.world.getServer();
            if (server != null) {
                transferPlayerToDimension((ServerPlayerEntity) player, dimensionId, server.getPlayerList());
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

    /**
     * Taken from CoFHCore's EntityHelper (https://github.com/CoFH/CoFHCore/blob/1.12/src/main/java/cofh/core/util/helpers/EntityHelper.java)
     * TODO We need ITeleporter for this and then get rid of these methods
     */
    private static void transferPlayerToDimension(ServerPlayerEntity player, int dimension, PlayerList manager) {
        int oldDim = player.dimension;
        ServerWorld oldWorld = manager.getServer().getWorld(player.dimension);
        player.dimension = dimension;
        ServerWorld newWorld = manager.getServer().getWorld(player.dimension);
        player.connection.sendPacket(new SRespawnPacket(player.dimension, newWorld.getDifficulty(), newWorld.getWorldInfo().getTerrainType(), player.interactionManager.getGameType()));
        oldWorld.removeEntityDangerously(player);
        if (player.isBeingRidden()) {
            player.removePassengers();
        }
        if (player.isRiding()) {
            player.dismountRidingEntity();
        }
        player.isDead = false;
        transferEntityToWorld(player, oldWorld, newWorld);
        manager.preparePlayer(player, oldWorld);
        player.connection.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
        player.interactionManager.setWorld(newWorld);
        manager.updateTimeAndWeatherForPlayer(player, newWorld);
        manager.syncPlayerInventory(player);

        for (PotionEffect potioneffect : player.getActivePotionEffects()) {
            player.connection.sendPacket(new SPacketEntityEffect(player.getEntityId(), potioneffect));
        }
        FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, oldDim, dimension);
    }

    /**
     * Taken from CoFHCore's EntityHelper (https://github.com/CoFH/CoFHCore/blob/1.12/src/main/java/cofh/core/util/helpers/EntityHelper.java)
     */
    private static void transferEntityToWorld(Entity entity, WorldServer oldWorld, WorldServer newWorld) {
        WorldProvider oldWorldProvider = oldWorld.provider;
        WorldProvider newWorldProvider = newWorld.provider;
        double moveFactor = oldWorldProvider.getMovementFactor() / newWorldProvider.getMovementFactor();
        double x = entity.posX * moveFactor;
        double z = entity.posZ * moveFactor;

        oldWorld.profiler.startSection("placing");
        x = MathHelper.clamp(x, -29999872, 29999872);
        z = MathHelper.clamp(z, -29999872, 29999872);
        if (entity.isEntityAlive()) {
            entity.setLocationAndAngles(x, entity.posY, z, entity.rotationYaw, entity.rotationPitch);
            newWorld.spawnEntity(entity);
            newWorld.updateEntityWithOptionalForce(entity, false);
        }
        oldWorld.profiler.endSection();

        entity.setWorld(newWorld);
    }

    public static void sendTeleportEffect(World world, BlockPos pos) {
        NetworkHandler.channel.sendToAllAround(new MessageTeleportEffect(pos), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
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
