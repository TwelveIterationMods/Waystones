package net.blay09.mods.waystones.api;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Unit;
import net.blay09.mods.waystones.core.WarpMode;
import net.blay09.mods.waystones.core.WaystoneTeleportContext;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WaystonesAPI {

    public static InternalMethods __internalMethods;

    public static Either<IWaystoneTeleportContext, WaystoneTeleportError> createDefaultTeleportContext(Entity entity, IWaystone waystone, WarpMode warpMode, @Nullable IWaystone fromWaystone) {
        return __internalMethods.createDefaultTeleportContext(entity, waystone, warpMode, fromWaystone);
    }

    public static Either<IWaystoneTeleportContext, WaystoneTeleportError> createCustomTeleportContext(Entity entity, IWaystone waystone) {
        return __internalMethods.createCustomTeleportContext(entity, waystone);
    }

    public static Either<List<Entity>, WaystoneTeleportError> tryTeleportToWaystone(Entity entity, IWaystone waystone, WarpMode warpMode, @Nullable IWaystone fromWaystone) {
        return __internalMethods.tryTeleportToWaystone(entity, waystone, warpMode, fromWaystone);
    }

    public static Either<List<Entity>, WaystoneTeleportError> tryTeleport(WaystoneTeleportContext context) {
        return __internalMethods.tryTeleport(context);
    }

    public static Either<List<Entity>, WaystoneTeleportError> forceTeleportToWaystone(Entity entity, IWaystone waystone) {
        return __internalMethods.forceTeleportToWaystone(entity, waystone);
    }

    public static List<Entity> forceTeleport(WaystoneTeleportContext context) {
        return __internalMethods.forceTeleport(context);
    }
}
