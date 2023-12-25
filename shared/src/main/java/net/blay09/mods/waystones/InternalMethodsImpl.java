package net.blay09.mods.waystones;

import com.mojang.datafixers.util.Either;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.*;
import net.blay09.mods.waystones.api.cost.*;
import net.blay09.mods.waystones.api.error.WaystoneTeleportError;
import net.blay09.mods.waystones.api.trait.IAttunementItem;
import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.block.WaystoneBlock;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.config.WaystonesConfigData;
import net.blay09.mods.waystones.core.*;
import net.blay09.mods.waystones.cost.CostContextImpl;
import net.blay09.mods.waystones.cost.CostRegistry;
import net.blay09.mods.waystones.cost.NoCost;
import net.blay09.mods.waystones.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class InternalMethodsImpl implements InternalMethods {

    @Override
    public Either<WaystoneTeleportContext, WaystoneTeleportError> createDefaultTeleportContext(Entity entity, Waystone waystone, Consumer<WaystoneTeleportContext> init) {
        return WaystonesAPI.createCustomTeleportContext(entity, waystone).ifLeft(context -> {
            final var shouldTransportPets = WaystonesConfig.getActive().restrictions.transportPets;
            if (shouldTransportPets == WaystonesConfigData.TransportPets.ENABLED || (shouldTransportPets == WaystonesConfigData.TransportPets.SAME_DIMENSION && !context.isDimensionalTeleport())) {
                context.getAdditionalEntities().addAll(WaystoneTeleportManager.findPets(entity));
            }
            context.getLeashedEntities().addAll(WaystoneTeleportManager.findLeashedAnimals(entity));
            init.accept(context);
            context.setCost(calculateCost(context));
        });
    }

    @Override
    public Either<WaystoneTeleportContext, WaystoneTeleportError> createCustomTeleportContext(Entity entity, Waystone waystone) {
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

        return Either.left(new WaystoneTeleportContextImpl(entity, waystone, waystone.resolveDestination(targetLevel)));
    }

    @Override
    public WaystoneTeleportContext createUnboundTeleportContext(Entity entity) {
        return new WaystoneTeleportContextImpl(entity,
                InvalidWaystone.INSTANCE,
                new TeleportDestination(entity.level(), entity.position(), entity.getDirection()));
    }

    @Override
    public Either<List<Entity>, WaystoneTeleportError> tryTeleport(WaystoneTeleportContext context) {
        return WaystoneTeleportManager.tryTeleport(context);
    }

    @Override
    public List<Entity> forceTeleport(WaystoneTeleportContext context) {
        return WaystoneTeleportManager.doTeleport(context);
    }

    @Override
    public Optional<Waystone> getWaystoneAt(Level level, BlockPos pos) {
        return WaystoneManagerImpl.get(level.getServer()).getWaystoneAt(level, pos);
    }

    @Override
    public Optional<Waystone> getWaystone(Level level, UUID uuid) {
        return WaystoneManagerImpl.get(level.getServer()).getWaystoneById(uuid);
    }

    @Override
    public ItemStack createAttunedShard(Waystone warpPlate) {
        ItemStack itemStack = new ItemStack(ModItems.attunedShard);
        setBoundWaystone(itemStack, warpPlate);
        return itemStack;
    }

    @Override
    public ItemStack createBoundScroll(Waystone waystone) {
        ItemStack itemStack = new ItemStack(ModItems.warpScroll);
        setBoundWaystone(itemStack, waystone);
        return itemStack;
    }

    @Override
    public Optional<Waystone> placeWaystone(Level level, BlockPos pos, WaystoneStyle style) {
        Block block = Balm.getRegistries().getBlock(style.getBlockRegistryName());
        level.setBlock(pos, block.defaultBlockState().setValue(WaystoneBlock.HALF, DoubleBlockHalf.LOWER), 3);
        level.setBlock(pos.above(), block.defaultBlockState().setValue(WaystoneBlock.HALF, DoubleBlockHalf.UPPER), 3);
        return getWaystoneAt(level, pos);
    }

    @Override
    public Optional<Waystone> placeSharestone(Level level, BlockPos pos, @Nullable DyeColor color) {
        Block sharestone = color != null ? ModBlocks.scopedSharestones[color.ordinal()] : ModBlocks.sharestone;
        level.setBlock(pos, sharestone.defaultBlockState().setValue(WaystoneBlock.HALF, DoubleBlockHalf.LOWER), 3);
        level.setBlock(pos.above(), sharestone.defaultBlockState().setValue(WaystoneBlock.HALF, DoubleBlockHalf.UPPER), 3);
        return getWaystoneAt(level, pos);
    }

    @Override
    public Optional<Waystone> placeWarpPlate(Level level, BlockPos pos) {
        level.setBlock(pos, ModBlocks.warpPlate.defaultBlockState(), 3);
        return getWaystoneAt(level, pos);
    }

    @Override
    public Optional<Waystone> getBoundWaystone(Player player, ItemStack itemStack) {
        if (itemStack.getItem() instanceof IAttunementItem attunementItem) {
            return Optional.ofNullable(attunementItem.getWaystoneAttunedTo(Balm.getHooks().getServer(), player, itemStack));
        }
        return Optional.empty();
    }

    @Override
    public void setBoundWaystone(ItemStack itemStack, @Nullable Waystone waystone) {
        if (itemStack.getItem() instanceof IAttunementItem attunementItem) {
            attunementItem.setWaystoneAttunedTo(itemStack, waystone);
        }
    }

    @Override
    public Cost calculateCost(WaystoneTeleportContext context) {
        if (!WaystonesConfig.getActive().costs.enableCosts) {
            return NoCost.INSTANCE;
        }

        final var costContext = new CostContextImpl(context);
        final var configuredModifiers = WaystonesConfig.getActive().costs.costModifiers;
        for (final var modifier : configuredModifiers) {
            CostRegistry.deserializeModifier(modifier).ifPresent(costContext::apply);
        }

        return costContext.resolve();
    }

    @Override
    public void registerCostType(CostType<?> costType) {
        CostRegistry.register(costType);
    }

    @Override
    public void registerCostModifier(CostModifier<?, ?> costModifier) {
        CostRegistry.register(costModifier);
    }

    @Override
    public void registerCostVariableResolver(CostVariableResolver costVariableResolver) {
        CostRegistry.register(costVariableResolver);
    }

    @Override
    public void registerCostConditionPredicate(CostConditionResolver costConditionResolver) {
        CostRegistry.register(costConditionResolver);
    }

    @Override
    public void registerCostParameterSerializer(CostParameterSerializer<?> costParameterSerializer) {
        CostRegistry.register(costParameterSerializer);
    }
}
