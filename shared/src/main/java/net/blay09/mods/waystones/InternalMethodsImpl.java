package net.blay09.mods.waystones;

import com.mojang.datafixers.util.Either;
import net.blay09.mods.waystones.api.*;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WarpMode;
import net.blay09.mods.waystones.core.WaystoneTeleportContext;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InternalMethodsImpl implements InternalMethods {

    @Override
    public Either<IWaystoneTeleportContext, WaystoneTeleportError> createDefaultTeleportContext(Entity entity, IWaystone waystone, WarpMode warpMode, @Nullable IWaystone fromWaystone) {
        return WaystonesAPI.createCustomTeleportContext(entity, waystone).ifLeft(context -> {
            context.setWarpMode(warpMode);
            context.getLeashedEntities().addAll(PlayerWaystoneManager.findLeashedAnimals(entity));
            context.setFromWaystone(fromWaystone);
            context.setWarpItem(PlayerWaystoneManager.findWarpItem(entity, warpMode));
            context.setCooldown(PlayerWaystoneManager.getCooldownPeriod(warpMode, waystone));

            // Use the context so far to determine the xp cost
            context.setXpCost(PlayerWaystoneManager.getExperienceLevelCost(entity, waystone, warpMode, context));
        });
    }

    @Override
    public Either<IWaystoneTeleportContext, WaystoneTeleportError> createCustomTeleportContext(Entity entity, IWaystone waystone) {
        if (!waystone.isValid()) {
            return Either.right(new WaystoneTeleportError.InvalidWaystone(waystone));
        }

        MinecraftServer server = entity.getServer();
        if (server == null) {
            return Either.right(new WaystoneTeleportError.NotOnServer());
        }

        ServerLevel targetLevel = server.getLevel(waystone.getDimension());
        if (targetLevel == null) {
            return Either.right(new WaystoneTeleportError.InvalidDimension(waystone.getDimension()));
        }

        if (!waystone.isValidInLevel(targetLevel)) {
            return Either.right(new WaystoneTeleportError.MissingWaystone(waystone));
        }

        return Either.left(new WaystoneTeleportContext(entity, waystone, waystone.resolveDestination(targetLevel)));
    }

    @Override
    public Either<List<Entity>, WaystoneTeleportError> tryTeleportToWaystone(Entity entity, IWaystone waystone, WarpMode warpMode, IWaystone fromWaystone) {
        return PlayerWaystoneManager.tryTeleportToWaystone(entity, waystone, warpMode, fromWaystone);
    }

    @Override
    public Either<List<Entity>, WaystoneTeleportError> tryTeleport(IWaystoneTeleportContext context) {
        return PlayerWaystoneManager.tryTeleport(context);
    }

    @Override
    public Either<List<Entity>, WaystoneTeleportError> forceTeleportToWaystone(Entity entity, IWaystone waystone) {
        return createDefaultTeleportContext(entity, waystone, WarpMode.CUSTOM, null).mapLeft(this::forceTeleport);
    }

    @Override
    public List<Entity> forceTeleport(IWaystoneTeleportContext context) {
        return PlayerWaystoneManager.doTeleport(context);
    }
}
