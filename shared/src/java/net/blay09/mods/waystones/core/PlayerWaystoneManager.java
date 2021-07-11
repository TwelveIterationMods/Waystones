package net.blay09.mods.waystones.core;

import com.google.common.collect.Lists;
import net.blay09.mods.balm.core.BalmSide;
import net.blay09.mods.balm.network.BalmNetworking;
import net.blay09.mods.waystones.ModEvents;
import net.blay09.mods.waystones.api.IMutableWaystone;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.api.WaystoneActivatedEvent;
import net.blay09.mods.waystones.block.WaystoneBlock;
import net.blay09.mods.waystones.block.WaystoneBlockBase;
import net.blay09.mods.waystones.block.entity.WarpPlateBlockEntity;
import net.blay09.mods.waystones.config.DimensionalWarp;
import net.blay09.mods.waystones.config.InventoryButtonMode;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.item.ModItems;
import net.blay09.mods.waystones.network.message.TeleportEffectMessage;
import net.blay09.mods.waystones.worldgen.namegen.NameGenerationMode;
import net.blay09.mods.waystones.worldgen.namegen.NameGenerator;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PlayerWaystoneManager {

    private static final Logger logger = LogManager.getLogger();

    private static final IPlayerWaystoneData persistentPlayerWaystoneData = new PersistentPlayerWaystoneData();
    private static final IPlayerWaystoneData inMemoryPlayerWaystoneData = new InMemoryPlayerWaystoneData();

    public static boolean mayBreakWaystone(Player player, BlockGetter world, BlockPos pos) {
        if (WaystonesConfig.getActive().restrictToCreative() && !player.getAbilities().instabuild) {
            return false;
        }

        return WaystoneManager.get(player.getServer()).getWaystoneAt(world, pos).map(waystone -> {

            if (!player.getAbilities().instabuild) {
                if (waystone.wasGenerated() && WaystonesConfig.getActive().generatedWaystonesUnbreakable()) {
                    return false;
                }

                boolean isGlobal = waystone.isGlobal();
                boolean mayBreakGlobalWaystones = !WaystonesConfig.getActive().globalWaystoneRequiresCreative();
                return !isGlobal || mayBreakGlobalWaystones;
            }


            return true;
        }).orElse(true);

    }

    public static boolean mayPlaceWaystone(@Nullable Player player) {
        return !WaystonesConfig.getActive().restrictToCreative() || (player != null && player.getAbilities().instabuild);
    }

    public static WaystoneEditPermissions mayEditWaystone(Player player, Level world, IWaystone waystone) {
        if (WaystonesConfig.getActive().restrictToCreative() && !player.getAbilities().instabuild) {
            return WaystoneEditPermissions.NOT_CREATIVE;
        }

        if (WaystonesConfig.getActive().restrictRenameToOwner() && !waystone.isOwner(player)) {
            return WaystoneEditPermissions.NOT_THE_OWNER;
        }

        if (waystone.isGlobal() && !player.getAbilities().instabuild && WaystonesConfig.getActive().globalWaystoneRequiresCreative()) {
            return WaystoneEditPermissions.GET_CREATIVE;
        }

        return WaystoneEditPermissions.ALLOW;
    }

    public static boolean isWaystoneActivated(Player player, IWaystone waystone) {
        return getPlayerWaystoneData(player.level).isWaystoneActivated(player, waystone);
    }

    public static void activateWaystone(Player player, IWaystone waystone) {
        if (!waystone.hasName() && waystone instanceof IMutableWaystone && waystone.wasGenerated()) {
            NameGenerationMode nameGenerationMode = WaystonesConfig.getActive().nameGenerationMode();
            String name = NameGenerator.get(player.getServer()).getName(waystone, player.level.random, nameGenerationMode);
            ((IMutableWaystone) waystone).setName(name);
        }

        if (!waystone.hasOwner() && waystone instanceof IMutableWaystone) {
            ((IMutableWaystone) waystone).setOwnerUid(player.getUUID());
        }

        if (!isWaystoneActivated(player, waystone) && waystone.getWaystoneType().equals(WaystoneTypes.WAYSTONE)) {
            getPlayerWaystoneData(player.level).activateWaystone(player, waystone);

            ModEvents.WAYSTONE_ACTIVATED.invoke(new WaystoneActivatedEvent(player, waystone));
        }
    }

    public static int getExperienceLevelCost(Entity player, IWaystone waystone, WarpMode warpMode, @Nullable IWaystone fromWaystone) {
        WaystoneTeleportContext context = new WaystoneTeleportContext();
        context.setLeashedEntities(findLeashedAnimals(player));
        context.setFromWaystone(fromWaystone);
        return getExperienceLevelCost(player, waystone, warpMode, context);
    }

    public static int getExperienceLevelCost(Entity entity, IWaystone waystone, WarpMode warpMode, WaystoneTeleportContext context) {
        if (!(entity instanceof Player)) {
            return 0;
        }

        Player player = (Player) entity;

        if (context.getFromWaystone() != null && waystone.getWaystoneUid().equals(context.getFromWaystone().getWaystoneUid())) {
            return 0;
        }

        boolean enableXPCost = !player.getAbilities().instabuild;

        int xpForLeashed = WaystonesConfig.getActive().xpCostPerLeashed() * context.getLeashedEntities().size();

        if (waystone.getDimension() != player.level.dimension()) {
            return enableXPCost ? WaystonesConfig.getActive().dimensionalWarpXpCost() + xpForLeashed : 0;
        }

        double xpCostMultiplier = warpMode.getXpCostMultiplier();
        if (waystone.isGlobal()) {
            xpCostMultiplier *= WaystonesConfig.getActive().globalWaystoneXpCostMultiplier();
        }

        BlockPos pos = waystone.getPos();
        double dist = Math.sqrt(player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()));
        final double minimumXpCost = WaystonesConfig.getActive().minimumXpCost();
        final double maximumXpCost = WaystonesConfig.getActive().maximumXpCost();
        double xpLevelCost;
        if (WaystonesConfig.getActive().blocksPerXPLevel() > 0) {
            xpLevelCost = Mth.clamp(dist / (float) WaystonesConfig.getActive().blocksPerXPLevel(), minimumXpCost, maximumXpCost);

            if (WaystonesConfig.getActive().inverseXpCost()) {
                xpLevelCost = maximumXpCost - xpLevelCost;
            }
        } else {
            xpLevelCost = minimumXpCost;
        }

        return enableXPCost ? (int) Math.round((xpLevelCost + xpForLeashed) * xpCostMultiplier) : 0;
    }

    @Nullable
    public static IWaystone getInventoryButtonWaystone(Player player) {
        InventoryButtonMode inventoryButtonMode = WaystonesConfig.getActive().getInventoryButtonMode();
        if (inventoryButtonMode.isReturnToNearest()) {
            return PlayerWaystoneManager.getNearestWaystone(player);
        } else if (inventoryButtonMode.hasNamedTarget()) {
            return WaystoneManager.get(player.getServer()).findWaystoneByName(inventoryButtonMode.getNamedTarget()).orElse(null);
        }

        return null;
    }

    public static boolean canUseInventoryButton(Player player) {
        IWaystone waystone = getInventoryButtonWaystone(player);
        int xpLevelCost = waystone != null ? getExperienceLevelCost(player, waystone, WarpMode.INVENTORY_BUTTON, (IWaystone) null) : 0;
        return getInventoryButtonCooldownLeft(player) <= 0 && (xpLevelCost <= 0 || player.experienceLevel >= xpLevelCost);
    }

    public static boolean canUseWarpStone(Player player, ItemStack heldItem) {
        return getWarpStoneCooldownLeft(player) <= 0;
    }

    public static double getCooldownMultiplier(IWaystone waystone) {
        return waystone.isGlobal() ? WaystonesConfig.getActive().globalWaystoneCooldownMultiplier() : 1f;
    }

    private static void informPlayer(Entity entity, String translationKey) {
        if (entity instanceof Player) {
            TranslatableComponent chatComponent = new TranslatableComponent(translationKey);
            chatComponent.withStyle(ChatFormatting.RED);
            ((Player) entity).displayClientMessage(chatComponent, false);
        }
    }

    public static boolean tryTeleportToWaystone(Entity entity, IWaystone waystone, WarpMode warpMode, @Nullable IWaystone fromWaystone) {
        if (!waystone.isValid()) {
            logger.info("Rejected teleport to invalid waystone");
            return false;
        }

        ItemStack warpItem = findWarpItem(entity, warpMode);
        if (!canUseWarpMode(entity, warpMode, warpItem, fromWaystone)) {
            logger.info("Rejected teleport using warp mode {}", warpMode);
            return false;
        }

        if (!warpMode.getAllowTeleportPredicate().test(entity, waystone)) {
            logger.info("Rejected teleport due to predicate");
            return false;
        }

        boolean isDimensionalWarp = waystone.getDimension() != entity.level.dimension();
        if (isDimensionalWarp && !canDimensionalWarpBetween(entity, waystone)) {
            logger.info("Rejected dimensional teleport");
            informPlayer(entity, "chat.waystones.cannot_dimension_warp");
            return false;
        }

        List<Mob> leashed = findLeashedAnimals(entity);
        if (!leashed.isEmpty()) {
            if (!WaystonesConfig.getActive().transportLeashed()) {
                logger.info("Rejected teleport with leashed entities");
                informPlayer(entity, "chat.waystones.cannot_transport_leashed");
                return false;
            }

            List<ResourceLocation> forbidden = WaystonesConfig.getActive().leashedDenyList().stream().map(ResourceLocation::new).collect(Collectors.toList());
            if (leashed.stream().anyMatch(e -> forbidden.contains(Registry.ENTITY_TYPE.getKey(e.getType())))) {
                logger.info("Rejected teleport with denied leashed entity");
                informPlayer(entity, "chat.waystones.cannot_transport_this_leashed");
                return false;
            }

            if (isDimensionalWarp && !WaystonesConfig.getActive().transportLeashedDimensional()) {
                logger.info("Rejected teleport with dimensionally denied leashed entity");
                informPlayer(entity, "chat.waystones.cannot_transport_leashed_dimensional");
                return false;
            }
        }

        MinecraftServer server = entity.getServer();
        if (server == null) {
            logger.info("Rejected teleport due to missing server");
            return false;
        }

        ServerLevel targetWorld = Objects.requireNonNull(server).getLevel(waystone.getDimension());
        BlockPos pos = waystone.getPos();
        BlockState state = targetWorld != null ? targetWorld.getBlockState(pos) : null;
        if (targetWorld == null || !(state.getBlock() instanceof WaystoneBlockBase)) {
            logger.info("Rejected teleport due to missing waystone");
            informPlayer(entity, "chat.waystones.waystone_missing");
            return false;
        }

        Direction direction = state.getValue(WaystoneBlock.FACING);
        // Use a list to keep order intact - it might check one direction twice, but no one cares
        List<Direction> directionCandidates = Lists.newArrayList(direction, Direction.EAST, Direction.WEST, Direction.SOUTH, Direction.NORTH);
        for (Direction candidate : directionCandidates) {
            BlockPos offsetPos = pos.relative(candidate);
            BlockPos offsetPosUp = offsetPos.above();
            if (targetWorld.getBlockState(offsetPos).isSuffocating(targetWorld, offsetPos) || targetWorld.getBlockState(offsetPosUp).isSuffocating(targetWorld, offsetPosUp)) {
                continue;
            }

            direction = candidate;
            break;
        }

        WaystoneTeleportContext context = new WaystoneTeleportContext();
        context.setLeashedEntities(leashed);
        context.setDirection(direction);
        context.setTargetWorld(targetWorld);
        context.setFromWaystone(fromWaystone);

        int xpLevelCost = getExperienceLevelCost(entity, waystone, warpMode, context);
        if (entity instanceof Player && ((Player) entity).experienceLevel < xpLevelCost) {
            logger.info("Rejected teleport due to missing xp");
            return false;
        }

        boolean isCreativeMode = entity instanceof Player && ((Player) entity).getAbilities().instabuild;
        if (warpMode.consumesItem() && !isCreativeMode) {
            warpItem.shrink(1);
        }

        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (warpMode == WarpMode.INVENTORY_BUTTON) {
                int cooldown = (int) (WaystonesConfig.getActive().inventoryButtonCooldown() * getCooldownMultiplier(waystone));
                getPlayerWaystoneData(entity.level).setInventoryButtonCooldownUntil(player, player.level.getGameTime() + cooldown * 20L);
                WaystoneSyncManager.sendWaystoneCooldowns(player);
            } else if (warpMode == WarpMode.WARP_STONE) {
                int cooldown = (int) (WaystonesConfig.getActive().warpStoneCooldown() * getCooldownMultiplier(waystone));
                getPlayerWaystoneData(entity.level).setWarpStoneCooldownUntil(player, player.level.getGameTime() + cooldown * 20L);
                WaystoneSyncManager.sendWaystoneCooldowns(player);
            }

            if (xpLevelCost > 0) {
                player.giveExperienceLevels(-xpLevelCost);
            }
        }

        teleportToWaystone(entity, waystone, context);
        return true;
    }

    private static boolean canDimensionalWarpBetween(Entity player, IWaystone waystone) {
        ResourceLocation fromDimension = player.level.dimension().location();
        ResourceLocation toDimension = waystone.getDimension().location();
        Collection<String> dimensionAllowList = WaystonesConfig.getActive().dimensionalWarpAllowList();
        Collection<String> dimensionDenyList = WaystonesConfig.getActive().dimensionalWarpDenyList();
        if (!dimensionAllowList.isEmpty() && (!dimensionAllowList.contains(toDimension.toString()) || !dimensionAllowList.contains(fromDimension.toString()))) {
            return false;
        } else if (!dimensionDenyList.isEmpty() && (dimensionDenyList.contains(toDimension.toString()) || dimensionDenyList.contains(fromDimension.toString()))) {
            return false;
        }

        DimensionalWarp dimensionalWarpMode = WaystonesConfig.getActive().dimensionalWarp();
        return dimensionalWarpMode == DimensionalWarp.ALLOW || dimensionalWarpMode == DimensionalWarp.GLOBAL_ONLY && waystone.isGlobal();
    }

    private static ItemStack findWarpItem(Entity entity, WarpMode warpMode) {
        switch (warpMode) {
            case WARP_SCROLL:
                return findWarpItem(entity, ModItems.warpScroll);
            case WARP_STONE:
                return findWarpItem(entity, ModItems.warpStone);
            case RETURN_SCROLL:
                return findWarpItem(entity, ModItems.returnScroll);
            case BOUND_SCROLL:
                return findWarpItem(entity, ModItems.boundScroll);
            default:
                return ItemStack.EMPTY;
        }
    }

    private static ItemStack findWarpItem(Entity entity, Item warpItem) {
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = ((LivingEntity) entity);
            if (livingEntity.getMainHandItem().getItem() == warpItem) {
                return livingEntity.getMainHandItem();
            } else if (livingEntity.getOffhandItem().getItem() == warpItem) {
                return livingEntity.getOffhandItem();
            }
        }

        return ItemStack.EMPTY;
    }

    private static List<Mob> findLeashedAnimals(Entity player) {
        return player.level.getEntitiesOfClass(Mob.class, new AABB(player.blockPosition()).inflate(10),
                e -> player.equals(e.getLeashHolder())
        );
    }

    private static void teleportToWaystone(Entity entity, IWaystone waystone, WaystoneTeleportContext context) {
        ServerLevel sourceWorld = (ServerLevel) entity.level;
        BlockPos sourcePos = entity.blockPosition();
        BlockPos pos = waystone.getPos();
        Direction direction = context.getDirection();
        ServerLevel targetWorld = context.getTargetWorld();
        BlockPos targetPos = waystone.getWaystoneType().equals(WaystoneTypes.WARP_PLATE) ? pos : pos.relative(direction);
        Vec3 targetPos3d = new Vec3(targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5);

        Entity mount = entity.getVehicle();
        if (mount != null) {
            entity.stopRiding();
            mount = teleportEntity(mount, targetWorld, targetPos3d, direction);
        }

        BlockEntity targetTileEntity = context.getTargetWorld().getBlockEntity(waystone.getPos());
        if (targetTileEntity instanceof WarpPlateBlockEntity) {
            ((WarpPlateBlockEntity) targetTileEntity).markEntityForCooldown(entity);

            if (mount != null) {
                ((WarpPlateBlockEntity) targetTileEntity).markEntityForCooldown(mount);
            }

            context.getLeashedEntities().forEach(((WarpPlateBlockEntity) targetTileEntity)::markEntityForCooldown);
        }

        entity = teleportEntity(entity, targetWorld, targetPos3d, direction);

        if (mount != null) {
            entity.startRiding(mount);
        }

        sourceWorld.playSound(null, sourcePos, SoundEvents.PORTAL_TRAVEL, SoundSource.PLAYERS, 0.5f, 1f);
        targetWorld.playSound(null, targetPos, SoundEvents.PORTAL_TRAVEL, SoundSource.PLAYERS, 0.5f, 1f);

        BalmNetworking.sendToTracking(sourceWorld, sourcePos, new TeleportEffectMessage(sourcePos));
        BalmNetworking.sendToTracking(targetWorld, targetPos, new TeleportEffectMessage(targetPos));

        context.getLeashedEntities().forEach(mob -> teleportEntity(mob, targetWorld, targetPos3d, direction));
    }

    private static Entity teleportEntity(Entity entity, ServerLevel targetWorld, Vec3 targetPos3d, Direction direction) {
        float yaw = direction.toYRot();
        double x = targetPos3d.x;
        double y = targetPos3d.y;
        double z = targetPos3d.z;
        if (entity instanceof ServerPlayer) {
            ChunkPos chunkPos = new ChunkPos(new BlockPos(x, y, z));
            targetWorld.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkPos, 1, entity.getId());
            entity.stopRiding();
            if (((ServerPlayer) entity).isSleeping()) {
                ((ServerPlayer) entity).stopSleepInBed(true, true);
            }

            if (targetWorld == entity.level) {
                ((ServerPlayer) entity).connection.teleport(x, y, z, yaw, entity.getXRot(), Collections.emptySet());
            } else {
                ((ServerPlayer) entity).teleportTo(targetWorld, x, y, z, yaw, entity.getXRot());
            }

            entity.setYHeadRot(yaw);
        } else {
            float pitch = Mth.clamp(entity.getXRot(), -90.0F, 90.0F);
            if (targetWorld == entity.level) {
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

        return entity;
    }

    public static void deactivateWaystone(Player player, IWaystone waystone) {
        getPlayerWaystoneData(player.level).deactivateWaystone(player, waystone);
    }

    private static boolean canUseWarpMode(Entity entity, WarpMode warpMode, ItemStack heldItem, @Nullable IWaystone fromWaystone) {
        switch (warpMode) {
            case INVENTORY_BUTTON:
                return entity instanceof Player && PlayerWaystoneManager.canUseInventoryButton(((Player) entity));
            case WARP_SCROLL:
                return !heldItem.isEmpty() && heldItem.getItem() == ModItems.warpScroll;
            case BOUND_SCROLL:
                return !heldItem.isEmpty() && heldItem.getItem() == ModItems.boundScroll;
            case RETURN_SCROLL:
                return !heldItem.isEmpty() && heldItem.getItem() == ModItems.returnScroll;
            case WARP_STONE:
                return !heldItem.isEmpty() && heldItem.getItem() == ModItems.warpStone && entity instanceof Player && PlayerWaystoneManager.canUseWarpStone(((Player) entity), heldItem);
            case WAYSTONE_TO_WAYSTONE:
                return WaystonesConfig.getActive().allowWaystoneToWaystoneTeleport() && fromWaystone != null && fromWaystone.isValid() && fromWaystone.getWaystoneType().equals(WaystoneTypes.WAYSTONE);
            case SHARESTONE_TO_SHARESTONE:
                return fromWaystone != null && fromWaystone.isValid() && WaystoneTypes.isSharestone(fromWaystone.getWaystoneType());
            case WARP_PLATE:
                return fromWaystone != null && fromWaystone.isValid() && fromWaystone.getWaystoneType().equals(WaystoneTypes.WARP_PLATE);
            case PORTSTONE_TO_WAYSTONE:
                return fromWaystone != null && fromWaystone.isValid() && fromWaystone.getWaystoneType().equals(WaystoneTypes.PORTSTONE);
        }

        return false;
    }

    public static long getWarpStoneCooldownUntil(Player player) {
        return getPlayerWaystoneData(player.level).getWarpStoneCooldownUntil(player);
    }

    public static long getWarpStoneCooldownLeft(Player player) {
        long cooldownUntil = getWarpStoneCooldownUntil(player);
        return Math.max(0, cooldownUntil - player.level.getGameTime());
    }

    public static void setWarpStoneCooldownUntil(Player player, long timeStamp) {
        getPlayerWaystoneData(player.level).setWarpStoneCooldownUntil(player, timeStamp);
    }

    public static long getInventoryButtonCooldownUntil(Player player) {
        return getPlayerWaystoneData(player.level).getInventoryButtonCooldownUntil(player);
    }

    public static long getInventoryButtonCooldownLeft(Player player) {
        long cooldownUntil = getInventoryButtonCooldownUntil(player);
        return Math.max(0, cooldownUntil - player.level.getGameTime());
    }

    public static void setInventoryButtonCooldownUntil(Player player, long timeStamp) {
        getPlayerWaystoneData(player.level).setInventoryButtonCooldownUntil(player, timeStamp);
    }

    @Nullable
    public static IWaystone getNearestWaystone(Player player) {
        return getPlayerWaystoneData(player.level).getWaystones(player).stream()
                .filter(it -> it.getDimension() == player.level.dimension())
                .min((first, second) -> {
                    double firstDist = first.getPos().distSqr(player.getX(), player.getY(), player.getZ(), true);
                    double secondDist = second.getPos().distSqr(player.getX(), player.getY(), player.getZ(), true);
                    return (int) Math.round(firstDist) - (int) Math.round(secondDist);
                }).orElse(null);
    }

    public static List<IWaystone> getWaystones(Player player) {
        return getPlayerWaystoneData(player.level).getWaystones(player);
    }

    public static IPlayerWaystoneData getPlayerWaystoneData(@Nullable Level world) {
        return world == null || world.isClientSide ? inMemoryPlayerWaystoneData : persistentPlayerWaystoneData;
    }

    public static IPlayerWaystoneData getPlayerWaystoneData(BalmSide side) {
        return side.isClient() ? inMemoryPlayerWaystoneData : persistentPlayerWaystoneData;
    }

    public static boolean mayTeleportToWaystone(Player player, IWaystone waystone) {
        return true;
    }

    public static void swapWaystoneSorting(Player player, int index, int otherIndex) {
        getPlayerWaystoneData(player.level).swapWaystoneSorting(player, index, otherIndex);
    }

    public static boolean mayEditGlobalWaystones(Player player) {
        return player.getAbilities().instabuild || !WaystonesConfig.getActive().globalWaystoneRequiresCreative();
    }

    public static void activeWaystoneForEveryone(@Nullable MinecraftServer server, IWaystone waystone) {
        if (server == null) {
            return;
        }

        List<ServerPlayer> players = server.getPlayerList().getPlayers();
        for (ServerPlayer player : players) {
            if (!isWaystoneActivated(player, waystone)) {
                activateWaystone(player, waystone);
            }
        }
    }

    public static void removeKnownWaystone(@Nullable MinecraftServer server, IWaystone waystone) {
        if (server == null) {
            return;
        }

        List<ServerPlayer> players = server.getPlayerList().getPlayers();
        for (ServerPlayer player : players) {
            deactivateWaystone(player, waystone);
            WaystoneSyncManager.sendActivatedWaystones(player);
        }
    }

}
