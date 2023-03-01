package net.blay09.mods.waystones.api;

import com.mojang.datafixers.util.Either;
import net.blay09.mods.waystones.core.WarpMode;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface InternalMethods {

    Either<IWaystoneTeleportContext, WaystoneTeleportError> createDefaultTeleportContext(Entity entity, IWaystone waystone, WarpMode warpMode, @Nullable IWaystone fromWaystone);

    Either<IWaystoneTeleportContext, WaystoneTeleportError> createCustomTeleportContext(Entity entity, IWaystone waystone);

    Either<List<Entity>, WaystoneTeleportError> tryTeleportToWaystone(Entity entity, IWaystone waystone, WarpMode warpMode, IWaystone fromWaystone);

    Either<List<Entity>, WaystoneTeleportError> tryTeleport(IWaystoneTeleportContext context);

    Either<List<Entity>, WaystoneTeleportError> forceTeleportToWaystone(Entity entity, IWaystone waystone);

    List<Entity> forceTeleport(IWaystoneTeleportContext context);
}
