package net.blay09.mods.waystones.core;

import com.mojang.datafixers.util.Either;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.*;
import net.blay09.mods.waystones.api.cost.Cost;
import net.blay09.mods.waystones.block.entity.WarpPlateBlockEntity;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.network.message.TeleportEffectMessage;
import net.blay09.mods.waystones.xp.ExperienceLevelCost;
import net.blay09.mods.waystones.xp.ExperiencePointsCost;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WaystoneTeleportManager {

    public static Collection<? extends Entity> findPets(Entity entity) {
        return entity.level().getEntitiesOfClass(TamableAnimal.class, new AABB(entity.blockPosition()).inflate(10),
                pet -> entity.getUUID().equals(pet.getOwnerUUID()) && !pet.isOrderedToSit()
        );
    }

    public static List<Mob> findLeashedAnimals(Entity player) {
        return player.level().getEntitiesOfClass(Mob.class, new AABB(player.blockPosition()).inflate(10),
                e -> player.equals(e.getLeashHolder())
        );
    }

    public static Cost predictExperienceLevelCost(Entity player, IWaystone waystone, WarpMode warpMode, @Nullable IWaystone fromWaystone) {
        WaystoneTeleportContext context = new WaystoneTeleportContext(player, waystone, null);
        context.getLeashedEntities().addAll(WaystoneTeleportManager.findLeashedAnimals(player));
        context.setFromWaystone(fromWaystone);
        context.setWarpMode(warpMode);
        return WaystonesAPI.calculateCost(context);
    }

    public static List<Entity> doTeleport(IWaystoneTeleportContext context) {
        List<Entity> teleportedEntities = teleportEntityAndAttached(context.getEntity(), context);
        context.getAdditionalEntities().forEach(additionalEntity -> teleportedEntities.addAll(teleportEntityAndAttached(additionalEntity, context)));

        ServerLevel sourceWorld = (ServerLevel) context.getEntity().level();
        BlockPos sourcePos = context.getEntity().blockPosition();

        final var destination = context.getDestination();
        final var targetLevel = destination.getLevel();
        final var targetPos = BlockPos.containing(destination.getLocation());

        BlockEntity targetTileEntity = targetLevel.getBlockEntity(targetPos);
        if (targetTileEntity instanceof WarpPlateBlockEntity warpPlate) {
            teleportedEntities.forEach(warpPlate::markEntityForCooldown);
        }

        if (context.playsSound()) {
            sourceWorld.playSound(context.getEntity(), sourcePos, SoundEvents.PORTAL_TRAVEL, SoundSource.PLAYERS, 0.01f, 1f);
            targetLevel.playSound(null, targetPos, SoundEvents.PORTAL_TRAVEL, SoundSource.PLAYERS, 0.05f, 1f);
        }

        if (context.playsEffect()) {
            teleportedEntities.forEach(additionalEntity -> Balm.getNetworking().sendToTracking(sourceWorld, sourcePos, new TeleportEffectMessage(sourcePos)));
            Balm.getNetworking().sendToTracking(targetLevel, targetPos, new TeleportEffectMessage(targetPos));
        }

        return teleportedEntities;
    }

    private static List<Entity> teleportEntityAndAttached(Entity entity, IWaystoneTeleportContext context) {
        final var teleportedEntities = new ArrayList<Entity>();

        final var destination = context.getDestination();
        final var targetLevel = destination.getLevel();
        final var targetLocation = destination.getLocation();
        final var targetDirection = destination.getDirection();

        final var mount = entity.getVehicle();
        Entity teleportedMount = null;
        if (mount != null) {
            teleportedMount = teleportEntity(mount, targetLevel, targetLocation, targetDirection);
            teleportedEntities.add(teleportedMount);
        }

        final List<Mob> leashedEntities = context.getLeashedEntities();
        final List<Entity> teleportedLeashedEntities = new ArrayList<>();
        leashedEntities.forEach(leashedEntity -> {
            Entity teleportedLeashedEntity = teleportEntity(leashedEntity, targetLevel, targetLocation, targetDirection);
            teleportedEntities.add(teleportedLeashedEntity);
            teleportedLeashedEntities.add(teleportedLeashedEntity);
        });

        final var teleportedEntity = teleportEntity(entity, targetLevel, targetLocation, targetDirection);
        teleportedEntities.add(teleportedEntity);

        // We have to update the leashedToEntity in case the player was cloned during dimensional teleport
        teleportedLeashedEntities.forEach(teleportedLeashedEntity -> {
            if (teleportedLeashedEntity instanceof Mob teleportedLeashedMob) {
                teleportedLeashedMob.setLeashedTo(teleportedEntity, true);
            }
        });

        if (teleportedMount != null) {
            // TODO We do not remount currently. It causes weird sync issues and it seems that Vanilla does not do it either.
            //      Would have to look further at what point it's safe to remount without triggering movement correction.
        }

        return teleportedEntities;
    }

    private static Entity teleportEntity(Entity entity, ServerLevel targetWorld, Vec3 targetPos3d, Direction direction) {
        float yaw = direction.toYRot();
        double x = targetPos3d.x;
        double y = targetPos3d.y;
        double z = targetPos3d.z;
        if (entity instanceof ServerPlayer) {
            ChunkPos chunkPos = new ChunkPos(BlockPos.containing(x, y, z));
            targetWorld.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkPos, 1, entity.getId());
            entity.stopRiding();
            if (((ServerPlayer) entity).isSleeping()) {
                ((ServerPlayer) entity).stopSleepInBed(true, true);
            }

            if (targetWorld == entity.level()) {
                ((ServerPlayer) entity).connection.teleport(x, y, z, yaw, entity.getXRot(), Collections.emptySet());
            } else {
                ((ServerPlayer) entity).teleportTo(targetWorld, x, y, z, yaw, entity.getXRot());
            }

            entity.setYHeadRot(yaw);
        } else {
            float pitch = Mth.clamp(entity.getXRot(), -90.0F, 90.0F);
            if (targetWorld == entity.level()) {
                entity.moveTo(x, y, z, yaw, pitch);
                entity.setYHeadRot(yaw);
            } else {
                entity.unRide();
                Entity oldEntity = entity;
                entity = entity.getType().create(targetWorld);
                if (entity == null) {
                    return oldEntity;
                }

                entity.restoreFrom(oldEntity);
                entity.moveTo(x, y, z, yaw, pitch);
                entity.setYHeadRot(yaw);
                oldEntity.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
                targetWorld.addDuringTeleport(entity);
            }
        }

        if (!(entity instanceof LivingEntity) || !((LivingEntity) entity).isFallFlying()) {
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(1, 0, 1));
            entity.setOnGround(true);
        }

        if (entity instanceof PathfinderMob) {
            ((PathfinderMob) entity).getNavigation().stop();
        }

        sendHackySyncPacketsAfterTeleport(entity);

        return entity;
    }

    private static void sendHackySyncPacketsAfterTeleport(Entity entity) {
        if (entity instanceof ServerPlayer player) {
            // No idea why this is still needed since we're using the same code as /tp. Maybe /tp is broken too for interdimensional travel.
            player.connection.send(new ClientboundSetExperiencePacket(player.experienceProgress, player.totalExperience, player.experienceLevel));
        }
    }

    public static Either<List<Entity>, WaystoneTeleportError> tryTeleportToWaystone(Entity entity, IWaystone waystone, WarpMode warpMode, @Nullable IWaystone fromWaystone) {
        return WaystonesAPI.createDefaultTeleportContext(entity, waystone, warpMode, fromWaystone)
                .flatMap(WaystoneTeleportManager::tryTeleport)
                .ifRight(PlayerWaystoneManager.informRejectedTeleport(entity));
    }

    public static Either<List<Entity>, WaystoneTeleportError> tryTeleport(IWaystoneTeleportContext context) {
        WaystoneTeleportEvent.Pre event = new WaystoneTeleportEvent.Pre(context);
        Balm.getEvents().fireEvent(event);
        if (event.isCanceled()) {
            return Either.right(new WaystoneTeleportError.CancelledByEvent());
        }

        final var waystone = context.getTargetWaystone();
        final var entity = context.getEntity();
        final var warpMode = context.getWarpMode();
        if (!PlayerWaystoneManager.canUseWarpMode(entity, warpMode, context.getWarpItem(), context.getFromWaystone().orElse(null))) {
            return Either.right(new WaystoneTeleportError.WarpModeRejected());
        }

        if (context.isDimensionalTeleport() && !event.getDimensionalTeleportResult()
                .withDefault(() -> PlayerWaystoneManager.canDimensionalWarpBetween(entity, waystone))) {
            return Either.right(new WaystoneTeleportError.DimensionalWarpDenied());
        }

        if (!context.getLeashedEntities().isEmpty()) {
            if (!WaystonesConfig.getActive().restrictions.transportLeashed) {
                return Either.right(new WaystoneTeleportError.LeashedWarpDenied());
            }

            List<ResourceLocation> forbidden = WaystonesConfig.getActive().restrictions.leashedDenyList.stream().map(ResourceLocation::new).toList();
            if (context.getLeashedEntities().stream().anyMatch(e -> forbidden.contains(BuiltInRegistries.ENTITY_TYPE.getKey(e.getType())))) {
                return Either.right(new WaystoneTeleportError.SpecificLeashedWarpDenied());
            }

            if (context.isDimensionalTeleport() && !WaystonesConfig.getActive().restrictions.transportLeashedDimensional) {
                return Either.right(new WaystoneTeleportError.LeashedDimensionalWarpDenied());
            }
        }

        if (entity instanceof Player player && !context.getExperienceCost().canAfford(player) && !player.getAbilities().instabuild) {
            return Either.right(new WaystoneTeleportError.NotEnoughXp());
        }

        boolean isCreativeMode = entity instanceof Player && ((Player) entity).getAbilities().instabuild;
        if (!context.getWarpItem().isEmpty() && event.getConsumeItemResult().withDefault(() -> !isCreativeMode && context.consumesWarpItem())) {
            context.getWarpItem().shrink(1);
        }

        if (entity instanceof Player player) {
            PlayerWaystoneManager.applyCooldown(warpMode, player, context.getCooldown());
            context.getExperienceCost().consume(player);
        }

        final var teleportedEntities = doTeleport(context);

        Balm.getEvents().fireEvent(new WaystoneTeleportEvent.Post(context, teleportedEntities));

        return Either.left(teleportedEntities);
    }

}
