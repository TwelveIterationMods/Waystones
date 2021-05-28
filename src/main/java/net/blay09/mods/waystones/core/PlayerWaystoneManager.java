package net.blay09.mods.waystones.core;

import com.google.common.collect.Lists;
import net.blay09.mods.waystones.api.IMutableWaystone;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.api.WaystoneActivatedEvent;
import net.blay09.mods.waystones.block.WaystoneBlock;
import net.blay09.mods.waystones.block.WaystoneBlockBase;
import net.blay09.mods.waystones.config.DimensionalWarp;
import net.blay09.mods.waystones.config.InventoryButtonMode;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.item.ModItems;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.TeleportEffectMessage;
import net.blay09.mods.waystones.tileentity.WarpPlateTileEntity;
import net.blay09.mods.waystones.worldgen.namegen.NameGenerationMode;
import net.blay09.mods.waystones.worldgen.namegen.NameGenerator;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPlayEntityEffectPacket;
import net.minecraft.network.play.server.SSetPassengersPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PlayerWaystoneManager {

    private static final IPlayerWaystoneData persistentPlayerWaystoneData = new PersistentPlayerWaystoneData();
    private static final IPlayerWaystoneData inMemoryPlayerWaystoneData = new InMemoryPlayerWaystoneData();

    public static boolean mayBreakWaystone(PlayerEntity player, IBlockReader world, BlockPos pos) {
        if (WaystonesConfig.SERVER.restrictToCreative.get() && !player.abilities.isCreativeMode) {
            return false;
        }

        IWaystone waystone = WaystoneManager.get().getWaystoneAt(world, pos).orElseThrow(IllegalStateException::new);
        if (!player.abilities.isCreativeMode) {
            if (waystone.wasGenerated() && WaystonesConfig.SERVER.generatedWaystonesUnbreakable.get()) {
                return false;
            }

            boolean isGlobal = waystone.isGlobal();
            boolean mayBreakGlobalWaystones = !WaystonesConfig.SERVER.globalWaystoneRequiresCreative.get();
            return !isGlobal || mayBreakGlobalWaystones;
        }

        return true;
    }

    public static boolean mayPlaceWaystone(@Nullable PlayerEntity player) {
        return !WaystonesConfig.SERVER.restrictToCreative.get() || (player != null && player.abilities.isCreativeMode);
    }

    public static WaystoneEditPermissions mayEditWaystone(PlayerEntity player, World world, IWaystone waystone) {
        if (WaystonesConfig.SERVER.restrictToCreative.get() && !player.abilities.isCreativeMode) {
            return WaystoneEditPermissions.NOT_CREATIVE;
        }

        if (WaystonesConfig.SERVER.restrictRenameToOwner.get() && !waystone.isOwner(player)) {
            return WaystoneEditPermissions.NOT_THE_OWNER;
        }

        if (waystone.isGlobal() && !player.abilities.isCreativeMode && WaystonesConfig.SERVER.globalWaystoneRequiresCreative.get()) {
            return WaystoneEditPermissions.GET_CREATIVE;
        }

        return WaystoneEditPermissions.ALLOW;
    }

    public static boolean isWaystoneActivated(PlayerEntity player, IWaystone waystone) {
        return getPlayerWaystoneData(player.world).isWaystoneActivated(player, waystone);
    }

    public static void activateWaystone(PlayerEntity player, IWaystone waystone) {
        if (!waystone.hasName() && waystone instanceof IMutableWaystone && waystone.wasGenerated()) {
            NameGenerationMode nameGenerationMode = WaystonesConfig.COMMON.nameGenerationMode.get();
            String name = NameGenerator.get().getName(waystone, player.world.rand, nameGenerationMode);
            ((IMutableWaystone) waystone).setName(name);
        }

        if (!waystone.hasOwner() && waystone instanceof IMutableWaystone) {
            ((IMutableWaystone) waystone).setOwnerUid(player.getUniqueID());
        }

        if (!isWaystoneActivated(player, waystone) && waystone.getWaystoneType().equals(WaystoneTypes.WAYSTONE)) {
            getPlayerWaystoneData(player.world).activateWaystone(player, waystone);

            MinecraftForge.EVENT_BUS.post(new WaystoneActivatedEvent(player, waystone));
        }
    }

    public static int getExperienceLevelCost(Entity player, IWaystone waystone, WarpMode warpMode, @Nullable IWaystone fromWaystone) {
        WaystoneTeleportContext context = new WaystoneTeleportContext();
        context.setLeashedEntities(findLeashedAnimals(player));
        context.setFromWaystone(fromWaystone);
        return getExperienceLevelCost(player, waystone, warpMode, context);
    }

    public static int getExperienceLevelCost(Entity entity, IWaystone waystone, WarpMode warpMode, WaystoneTeleportContext context) {
        if (!(entity instanceof PlayerEntity)) {
            return 0;
        }

        PlayerEntity player = (PlayerEntity) entity;

        if (context.getFromWaystone() != null && waystone.getWaystoneUid().equals(context.getFromWaystone().getWaystoneUid())) {
            return 0;
        }

        boolean enableXPCost = !player.abilities.isCreativeMode;

        int xpForLeashed = WaystonesConfig.SERVER.costPerLeashed.get() * context.getLeashedEntities().size();

        if (waystone.getDimension() != player.world.getDimensionKey()) {
            return enableXPCost ? WaystonesConfig.SERVER.dimensionalWarpXpCost.get() + xpForLeashed : 0;
        }

        double xpCostMultiplier = warpMode.getXpCostMultiplier();
        if (waystone.isGlobal()) {
            xpCostMultiplier *= WaystonesConfig.SERVER.globalWaystoneXpCostMultiplier.get();
        }

        BlockPos pos = waystone.getPos();
        double dist = Math.sqrt(player.getDistanceSq(pos.getX(), pos.getY(), pos.getZ()));
        final double minimumXpCost = WaystonesConfig.SERVER.minimumXpCost.get();
        final double maximumXpCost = WaystonesConfig.SERVER.maximumXpCost.get();
        double xpLevelCost;
        if (WaystonesConfig.SERVER.blocksPerXPLevel.get() > 0) {
            xpLevelCost = MathHelper.clamp(dist / (float) WaystonesConfig.SERVER.blocksPerXPLevel.get(), minimumXpCost, maximumXpCost);

            if (WaystonesConfig.SERVER.inverseXpCost.get()) {
                xpLevelCost = maximumXpCost - xpLevelCost;
            }
        } else {
            xpLevelCost = minimumXpCost;
        }

        return enableXPCost ? (int) Math.round((xpLevelCost + xpForLeashed) * xpCostMultiplier) : 0;
    }

    @Nullable
    public static IWaystone getInventoryButtonWaystone(PlayerEntity player) {
        InventoryButtonMode inventoryButtonMode = WaystonesConfig.getInventoryButtonMode();
        if (inventoryButtonMode.isReturnToNearest()) {
            return PlayerWaystoneManager.getNearestWaystone(player);
        } else if (inventoryButtonMode.hasNamedTarget()) {
            return WaystoneManager.get().findWaystoneByName(inventoryButtonMode.getNamedTarget()).orElse(null);
        }

        return null;
    }

    public static boolean canUseInventoryButton(PlayerEntity player) {
        IWaystone waystone = getInventoryButtonWaystone(player);
        int xpLevelCost = waystone != null ? getExperienceLevelCost(player, waystone, WarpMode.INVENTORY_BUTTON, (IWaystone) null) : 0;
        return getInventoryButtonCooldownLeft(player) <= 0 && (xpLevelCost <= 0 || player.experienceLevel >= xpLevelCost);
    }

    public static boolean canUseWarpStone(PlayerEntity player, ItemStack heldItem) {
        return getWarpStoneCooldownLeft(player) <= 0;
    }

    public static double getCooldownMultiplier(IWaystone waystone) {
        return waystone.isGlobal() ? WaystonesConfig.SERVER.globalWaystoneCooldownMultiplier.get() : 1f;
    }

    private static void informPlayer(Entity entity, String translationKey) {
        if (entity instanceof PlayerEntity) {
            TranslationTextComponent chatComponent = new TranslationTextComponent(translationKey);
            chatComponent.mergeStyle(TextFormatting.RED);
            ((PlayerEntity) entity).sendStatusMessage(chatComponent, false);
        }
    }

    public static boolean tryTeleportToWaystone(Entity entity, IWaystone waystone, WarpMode warpMode, @Nullable IWaystone fromWaystone) {
        if (!waystone.isValid()) {
            return false;
        }

        ItemStack warpItem = findWarpItem(entity, warpMode);
        if (!canUseWarpMode(entity, warpMode, warpItem, fromWaystone)) {
            return false;
        }

        if (!warpMode.getAllowTeleportPredicate().test(entity, waystone)) {
            return false;
        }

        boolean isDimensionalWarp = waystone.getDimension() != entity.world.getDimensionKey();
        if (isDimensionalWarp && !canDimensionalWarpBetween(entity, waystone)) {
            informPlayer(entity, "chat.waystones.cannot_dimension_warp");
            return false;
        }

        List<MobEntity> leashed = findLeashedAnimals(entity);
        if (!leashed.isEmpty()) {
            if (!WaystonesConfig.SERVER.transportLeashed.get()) {
                informPlayer(entity, "chat.waystones.cannot_transport_leashed");
                return false;
            }

            List<ResourceLocation> forbidden = WaystonesConfig.SERVER.leashedBlacklist.get().stream().map(ResourceLocation::new).collect(Collectors.toList());
            if (leashed.stream().anyMatch(e -> forbidden.contains(e.getType().getRegistryName()))) {
                informPlayer(entity, "chat.waystones.cannot_transport_this_leashed");
                return false;
            }

            if (isDimensionalWarp && !WaystonesConfig.SERVER.transportLeashedDimensional.get()) {
                informPlayer(entity, "chat.waystones.cannot_transport_leashed_dimensional");
                return false;
            }
        }

        MinecraftServer server = entity.getServer();
        if (server == null) {
            return false;
        }

        ServerWorld targetWorld = Objects.requireNonNull(server).getWorld(waystone.getDimension());
        BlockPos pos = waystone.getPos();
        BlockState state = targetWorld != null ? targetWorld.getBlockState(pos) : null;
        if (targetWorld == null || !(state.getBlock() instanceof WaystoneBlockBase)) {
            informPlayer(entity, "chat.waystones.waystone_missing");
            return false;
        }

        Direction direction = state.get(WaystoneBlock.FACING);
        // Use a list to keep order intact - it might check one direction twice, but no one cares
        List<Direction> directionCandidates = Lists.newArrayList(direction, Direction.EAST, Direction.WEST, Direction.SOUTH, Direction.NORTH);
        for (Direction candidate : directionCandidates) {
            BlockPos offsetPos = pos.offset(candidate);
            BlockPos offsetPosUp = offsetPos.up();
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
        if (entity instanceof PlayerEntity && ((PlayerEntity) entity).experienceLevel < xpLevelCost) {
            return false;
        }

        boolean isCreativeMode = entity instanceof PlayerEntity && ((PlayerEntity) entity).abilities.isCreativeMode;
        if (warpMode.consumesItem() && !isCreativeMode) {
            warpItem.shrink(1);
        }

        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            if (warpMode == WarpMode.INVENTORY_BUTTON) {
                int cooldown = (int) (WaystonesConfig.SERVER.inventoryButtonCooldown.get() * getCooldownMultiplier(waystone));
                getPlayerWaystoneData(entity.world).setInventoryButtonCooldownUntil(player, player.world.getGameTime() + cooldown * 20L);
                WaystoneSyncManager.sendWaystoneCooldowns(player);
            } else if (warpMode == WarpMode.WARP_STONE) {
                int cooldown = (int) (WaystonesConfig.SERVER.warpStoneCooldown.get() * getCooldownMultiplier(waystone));
                getPlayerWaystoneData(entity.world).setWarpStoneCooldownUntil(player, player.world.getGameTime() + cooldown * 20L);
                WaystoneSyncManager.sendWaystoneCooldowns(player);
            }

            if (xpLevelCost > 0) {
                player.addExperienceLevel(-xpLevelCost);
            }
        }

        teleportToWaystone(entity, waystone, context);
        return true;
    }

    private static boolean canDimensionalWarpBetween(Entity player, IWaystone waystone) {
        ResourceLocation fromDimension = player.getEntityWorld().getDimensionKey().getLocation();
        ResourceLocation toDimension = waystone.getDimension().getLocation();
        List<? extends String> dimensionAllowList = WaystonesConfig.COMMON.dimensionalWarpAllowList.get();
        List<? extends String> dimensionDenyList = WaystonesConfig.COMMON.dimensionalWarpDenyList.get();
        if (!dimensionAllowList.isEmpty() && (!dimensionAllowList.contains(toDimension.toString()) || !dimensionAllowList.contains(fromDimension.toString()))) {
            return false;
        } else if (!dimensionDenyList.isEmpty() && (dimensionDenyList.contains(toDimension.toString()) || dimensionDenyList.contains(fromDimension.toString()))) {
            return false;
        }

        DimensionalWarp dimensionalWarpMode = WaystonesConfig.SERVER.dimensionalWarp.get();
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
            if (livingEntity.getHeldItemMainhand().getItem() == warpItem) {
                return livingEntity.getHeldItemMainhand();
            } else if (livingEntity.getHeldItemOffhand().getItem() == warpItem) {
                return livingEntity.getHeldItemOffhand();
            }
        }

        return ItemStack.EMPTY;
    }

    private static List<MobEntity> findLeashedAnimals(Entity player) {
        return player.world.getEntitiesWithinAABB(MobEntity.class, new AxisAlignedBB(player.getPosition()).grow(10),
                e -> player.equals(e.getLeashHolder())
        );
    }

    private static void teleportToWaystone(Entity entity, IWaystone waystone, WaystoneTeleportContext context) {
        BlockPos sourcePos = entity.getPosition();
        BlockPos pos = waystone.getPos();
        Direction direction = context.getDirection();
        ServerWorld targetWorld = context.getTargetWorld();
        BlockPos targetPos = waystone.getWaystoneType().equals(WaystoneTypes.WARP_PLATE) ? pos : pos.offset(direction);
        Vector3d targetPos3d = new Vector3d(targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5);

        Entity mount = entity.getRidingEntity();
        if (mount != null) {
            entity.stopRiding();
            if (targetWorld == mount.world) {
                mount.setPositionAndUpdate(targetPos3d.x, targetPos3d.y, targetPos3d.z);
            } else {
                mount = mount.changeDimension(targetWorld, new WaystoneTeleporter(targetPos3d));
            }
        }

        TileEntity targetTileEntity = context.getTargetWorld().getTileEntity(waystone.getPos());
        if (targetTileEntity instanceof WarpPlateTileEntity) {
            ((WarpPlateTileEntity) targetTileEntity).markEntityForCooldown(entity);

            if (mount != null) {
                ((WarpPlateTileEntity) targetTileEntity).markEntityForCooldown(mount);
            }

            context.getLeashedEntities().forEach(((WarpPlateTileEntity) targetTileEntity)::markEntityForCooldown);
        }

        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) entity;
            player.teleport(targetWorld, targetPos3d.getX(), targetPos.getY(), targetPos3d.getZ(), direction.getHorizontalAngle(), entity.rotationPitch);

            // When teleporting a player, we have to manually resync some things as teleport doesn't do it for us
            if (mount != null) {
                player.startRiding(mount);
                player.connection.sendPacket(new SSetPassengersPacket(mount));
            }

            for (EffectInstance effectinstance : player.getActivePotionEffects()) {
                player.connection.sendPacket(new SPlayEntityEffectPacket(entity.getEntityId(), effectinstance));
            }
            player.setExperienceLevel(player.experienceLevel);
        } else {
            teleportEntity(entity, targetWorld, targetPos3d);
        }

        NetworkHandler.channel.send(PacketDistributor.TRACKING_CHUNK.with(() -> entity.world.getChunkAt(sourcePos)), new TeleportEffectMessage(sourcePos));
        NetworkHandler.channel.send(PacketDistributor.TRACKING_CHUNK.with(() -> entity.world.getChunkAt(targetPos)), new TeleportEffectMessage(targetPos));

        context.getLeashedEntities().forEach(mob -> teleportEntity(mob, targetWorld, targetPos3d));
    }

    private static void teleportEntity(Entity entity, ServerWorld targetWorld, Vector3d targetPos3d) {
        if (targetWorld == entity.world) {
            entity.setPosition(targetPos3d.x, targetPos3d.y, targetPos3d.z);
        } else {
            entity.changeDimension(targetWorld, new WaystoneTeleporter(targetPos3d));
        }
    }

    public static void deactivateWaystone(PlayerEntity player, IWaystone waystone) {
        getPlayerWaystoneData(player.world).deactivateWaystone(player, waystone);
    }

    private static boolean canUseWarpMode(Entity entity, WarpMode warpMode, ItemStack heldItem, @Nullable IWaystone fromWaystone) {
        switch (warpMode) {
            case INVENTORY_BUTTON:
                return entity instanceof PlayerEntity && PlayerWaystoneManager.canUseInventoryButton(((PlayerEntity) entity));
            case WARP_SCROLL:
                return !heldItem.isEmpty() && heldItem.getItem() == ModItems.warpScroll;
            case BOUND_SCROLL:
                return !heldItem.isEmpty() && heldItem.getItem() == ModItems.boundScroll;
            case RETURN_SCROLL:
                return !heldItem.isEmpty() && heldItem.getItem() == ModItems.returnScroll;
            case WARP_STONE:
                return !heldItem.isEmpty() && heldItem.getItem() == ModItems.warpStone && entity instanceof PlayerEntity && PlayerWaystoneManager.canUseWarpStone(((PlayerEntity) entity), heldItem);
            case WAYSTONE_TO_WAYSTONE:
                return WaystonesConfig.COMMON.allowWaystoneToWaystoneTeleport.get() && fromWaystone != null && fromWaystone.isValid() && fromWaystone.getWaystoneType().equals(WaystoneTypes.WAYSTONE);
            case SHARESTONE_TO_SHARESTONE:
                return fromWaystone != null && fromWaystone.isValid() && WaystoneTypes.isSharestone(fromWaystone.getWaystoneType());
            case WARP_PLATE:
                return fromWaystone != null && fromWaystone.isValid() && fromWaystone.getWaystoneType().equals(WaystoneTypes.WARP_PLATE);
        }

        return false;
    }

    public static long getWarpStoneCooldownUntil(PlayerEntity player) {
        return getPlayerWaystoneData(player.world).getWarpStoneCooldownUntil(player);
    }

    public static long getWarpStoneCooldownLeft(PlayerEntity player) {
        long cooldownUntil = getWarpStoneCooldownUntil(player);
        return Math.max(0, cooldownUntil - player.world.getGameTime());
    }

    public static void setWarpStoneCooldownUntil(PlayerEntity player, long timeStamp) {
        getPlayerWaystoneData(player.world).setWarpStoneCooldownUntil(player, timeStamp);
    }

    public static long getInventoryButtonCooldownUntil(PlayerEntity player) {
        return getPlayerWaystoneData(player.world).getInventoryButtonCooldownUntil(player);
    }

    public static long getInventoryButtonCooldownLeft(PlayerEntity player) {
        long cooldownUntil = getInventoryButtonCooldownUntil(player);
        return Math.max(0, cooldownUntil - player.world.getGameTime());
    }

    public static void setInventoryButtonCooldownUntil(PlayerEntity player, long timeStamp) {
        getPlayerWaystoneData(player.world).setInventoryButtonCooldownUntil(player, timeStamp);
    }

    @Nullable
    public static IWaystone getNearestWaystone(PlayerEntity player) {
        return getPlayerWaystoneData(player.world).getWaystones(player).stream()
                .filter(it -> it.getDimension() == player.world.getDimensionKey())
                .min((first, second) -> {
                    double firstDist = first.getPos().distanceSq(player.getPosX(), player.getPosY(), player.getPosZ(), true);
                    double secondDist = second.getPos().distanceSq(player.getPosX(), player.getPosY(), player.getPosZ(), true);
                    return (int) Math.round(firstDist) - (int) Math.round(secondDist);
                }).orElse(null);
    }

    public static List<IWaystone> getWaystones(PlayerEntity player) {
        return getPlayerWaystoneData(player.world).getWaystones(player);
    }

    public static IPlayerWaystoneData getPlayerWaystoneData(World world) {
        return world.isRemote ? inMemoryPlayerWaystoneData : persistentPlayerWaystoneData;
    }

    public static IPlayerWaystoneData getPlayerWaystoneData(LogicalSide side) {
        return side.isClient() ? inMemoryPlayerWaystoneData : persistentPlayerWaystoneData;
    }

    public static boolean mayTeleportToWaystone(PlayerEntity player, IWaystone waystone) {
        return true;
    }

    public static void swapWaystoneSorting(PlayerEntity player, int index, int otherIndex) {
        getPlayerWaystoneData(player.world).swapWaystoneSorting(player, index, otherIndex);
    }

    public static boolean mayEditGlobalWaystones(PlayerEntity player) {
        return player.abilities.isCreativeMode || !WaystonesConfig.SERVER.globalWaystoneRequiresCreative.get();
    }

    public static void activeWaystoneForEveryone(IWaystone waystone) {
        List<ServerPlayerEntity> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
        for (ServerPlayerEntity player : players) {
            if (!isWaystoneActivated(player, waystone)) {
                activateWaystone(player, waystone);
            }
        }
    }

    public static void removeKnownWaystone(IWaystone waystone) {
        List<ServerPlayerEntity> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
        for (ServerPlayerEntity player : players) {
            deactivateWaystone(player, waystone);
            WaystoneSyncManager.sendKnownWaystones(player);
        }
    }

}
