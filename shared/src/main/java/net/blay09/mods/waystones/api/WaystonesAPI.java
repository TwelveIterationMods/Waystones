package net.blay09.mods.waystones.api;

import com.mojang.datafixers.util.Either;
import net.blay09.mods.waystones.api.cost.*;
import net.blay09.mods.waystones.api.error.WaystoneTeleportError;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class WaystonesAPI {

    public static InternalMethods __internalMethods;

    public static Either<WaystoneTeleportContext, WaystoneTeleportError> createDefaultTeleportContext(Entity entity, Waystone waystone, @Nullable Waystone fromWaystone) {
        return __internalMethods.createDefaultTeleportContext(entity, waystone, fromWaystone);
    }

    public static Either<WaystoneTeleportContext, WaystoneTeleportError> createCustomTeleportContext(Entity entity, Waystone waystone) {
        return __internalMethods.createCustomTeleportContext(entity, waystone);
    }

    public static Either<List<Entity>, WaystoneTeleportError> tryTeleport(WaystoneTeleportContext context) {
        return __internalMethods.tryTeleport(context);
    }

    public static List<Entity> forceTeleport(WaystoneTeleportContext context) {
        return __internalMethods.forceTeleport(context);
    }

    public static Optional<Waystone> getWaystoneAt(ServerLevel level, BlockPos pos) {
        return __internalMethods.getWaystoneAt(level, pos);
    }

    /**
     * @param level only used to access getServer() when on server, does not have to match the waystone's actual level
     */
    public static Optional<Waystone> getWaystone(Level level, UUID uuid) {
        return __internalMethods.getWaystone(level, uuid);
    }

    public static Optional<Waystone> placeWaystone(Level level, BlockPos pos, WaystoneStyle style) {
        return __internalMethods.placeWaystone(level, pos, style);
    }

    public static Optional<Waystone> placeSharestone(Level level, BlockPos pos, @Nullable DyeColor color) {
        return __internalMethods.placeSharestone(level, pos, color);
    }

    public static Optional<Waystone> placeWarpPlate(Level level, BlockPos pos) {
        return __internalMethods.placeWarpPlate(level, pos);
    }

    public static Optional<Waystone> getBoundWaystone(Player player, ItemStack itemStack) {
        return __internalMethods.getBoundWaystone(player, itemStack);
    }

    public static void setBoundWaystone(ItemStack itemStack, Waystone waystone) {
        __internalMethods.setBoundWaystone(itemStack, waystone);
    }

    public static ItemStack createAttunedShard(Waystone warpPlate) {
        return __internalMethods.createAttunedShard(warpPlate);
    }

    public static ItemStack createBoundScroll(Waystone waystone) {
        return __internalMethods.createBoundScroll(waystone);
    }

    public static Cost calculateCost(WaystoneTeleportContext context) {
        return __internalMethods.calculateCost(context);
    }

    public static void registerCostType(CostType<?> costType) {
        __internalMethods.registerCostType(costType);
    }

    public static void registerCostModifier(CostModifier<?, ?> costModifier) {
        __internalMethods.registerCostModifier(costModifier);
    }

    public static void registerCostVariableResolver(CostVariableResolver costVariableResolver) {
        __internalMethods.registerCostVariableResolver(costVariableResolver);
    }

    public static void registerCostConditionPredicate(CostConditionResolver costConditionResolver) {
        __internalMethods.registerCostConditionPredicate(costConditionResolver);
    }

    public static void registerCostParameterSerializer(CostParameterSerializer<?> costParameterSerializer) {
        __internalMethods.registerCostParameterSerializer(costParameterSerializer);
    }

}
