package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.api.WaystoneActivatedEvent;
import net.blay09.mods.waystones.block.WaystoneBlock;
import net.blay09.mods.waystones.config.DimensionalWarp;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.item.ModItems;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.TeleportEffectMessage;
import net.minecraft.block.BlockState;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPlayEntityEffectPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.server.MinecraftServer;
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
        getPlayerWaystoneData(player.world).activateWaystone(player, waystone);

        MinecraftForge.EVENT_BUS.post(new WaystoneActivatedEvent(player, waystone));
    }

    public static int getExperienceLevelCost(PlayerEntity player, IWaystone waystone, WarpMode warpMode) {
        WaystoneTeleportContext context = new WaystoneTeleportContext();
        context.setLeashedEntities(findLeashedAnimals(player));
        return getExperienceLevelCost(player, waystone, warpMode, context);
    }

    public static int getExperienceLevelCost(PlayerEntity player, IWaystone waystone, WarpMode warpMode, WaystoneTeleportContext context) {
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
        } else {
            xpLevelCost = minimumXpCost;
        }

        return enableXPCost ? (int) Math.round((xpLevelCost + xpForLeashed) * xpCostMultiplier) : 0;
    }


    public static boolean canUseInventoryButton(PlayerEntity player) {
        return getInventoryButtonCooldownLeft(player) <= 0;
    }

    public static boolean canUseWarpStone(PlayerEntity player, ItemStack heldItem) {
        return getWarpStoneCooldownLeft(player) <= 0;
    }

    public static double getCooldownMultiplier(IWaystone waystone) {
        return waystone.isGlobal() ? WaystonesConfig.SERVER.globalWaystoneCooldownMultiplier.get() : 1f;
    }

    private static void informPlayer(PlayerEntity player, String translationKey) {
        TranslationTextComponent chatComponent = new TranslationTextComponent(translationKey);
        chatComponent.mergeStyle(TextFormatting.RED);
        player.sendStatusMessage(chatComponent, false);
    }

    public static boolean tryTeleportToWaystone(ServerPlayerEntity player, IWaystone waystone, WarpMode warpMode, @Nullable IWaystone fromWaystone) {
        if (!waystone.isValid()) {
            return false;
        }

        ItemStack warpItem = findWarpItem(player, warpMode);
        if (!canUseWarpMode(player, warpMode, warpItem, fromWaystone)) {
            return false;
        }

        boolean isDimensionalWarp = waystone.getDimension() != player.world.getDimensionKey();
        if (isDimensionalWarp && !canDimensionalWarpTo(player, waystone)) {
            informPlayer(player, "chat.waystones.cannot_dimension_warp");
            return false;
        }

        List<MobEntity> leashed = findLeashedAnimals(player);
        if (!leashed.isEmpty()) {
            if (!WaystonesConfig.SERVER.transportLeashed.get()) {
                informPlayer(player, "chat.waystones.cannot_transport_leashed");
                return false;
            }

            List<ResourceLocation> forbidden = WaystonesConfig.SERVER.leashedBlacklist.get().stream().map(ResourceLocation::new).collect(Collectors.toList());
            if (leashed.stream().anyMatch(e -> forbidden.contains(e.getType().getRegistryName()))) {
                informPlayer(player, "chat.waystones.cannot_transport_this_leashed");
                return false;
            }

            if (isDimensionalWarp && !WaystonesConfig.SERVER.transportLeashedDimensional.get()) {
                informPlayer(player, "chat.waystones.cannot_transport_leashed_dimensional");
                return false;
            }
        }

        MinecraftServer server = player.getServer();
        ServerWorld targetWorld = Objects.requireNonNull(server).getWorld(waystone.getDimension());
        BlockState state = targetWorld != null ? targetWorld.getBlockState(waystone.getPos()) : null;
        if (targetWorld == null || !(state.getBlock() instanceof WaystoneBlock)) {
            TranslationTextComponent chatComponent = new TranslationTextComponent("chat.waystones.waystone_missing");
            chatComponent.mergeStyle(TextFormatting.RED);
            player.sendStatusMessage(chatComponent, false);
            WaystoneManager.get().removeWaystone(waystone);
            PlayerWaystoneManager.removeKnownWaystone(waystone);
            return false;
        }

        WaystoneTeleportContext context = new WaystoneTeleportContext();
        context.setLeashedEntities(leashed);
        context.setDirection(state.get(WaystoneBlock.FACING));
        context.setTargetWorld(targetWorld);

        int xpLevelCost = getExperienceLevelCost(player, waystone, warpMode, context);
        if (player.experienceLevel < xpLevelCost) {
            return false;
        }

        if (warpMode.consumesItem() && !player.abilities.isCreativeMode) {
            warpItem.shrink(1);
        }

        if (warpMode == WarpMode.INVENTORY_BUTTON) {
            int cooldown = (int) (WaystonesConfig.SERVER.inventoryButtonCooldown.get() * getCooldownMultiplier(waystone));
            getPlayerWaystoneData(player.world).setInventoryButtonCooldownUntil(player, System.currentTimeMillis() + cooldown * 1000);
            WaystoneSyncManager.sendWaystoneCooldowns(player);
        } else if (warpMode == WarpMode.WARP_STONE) {
            int cooldown = (int) (WaystonesConfig.SERVER.warpStoneCooldown.get() * getCooldownMultiplier(waystone));
            getPlayerWaystoneData(player.world).setWarpStoneCooldownUntil(player, System.currentTimeMillis() + cooldown * 1000);
            WaystoneSyncManager.sendWaystoneCooldowns(player);
        }

        if (xpLevelCost > 0) {
            player.addExperienceLevel(-xpLevelCost);
        }

        teleportToWaystone(player, waystone, context);
        return true;
    }

    private static boolean canDimensionalWarpTo(PlayerEntity player, IWaystone waystone) {
        ResourceLocation dimension = waystone.getDimension().getLocation();
        List<? extends String> dimensionAllowList = WaystonesConfig.COMMON.dimensionalWarpAllowList.get();
        List<? extends String> dimensionDenyList = WaystonesConfig.COMMON.dimensionalWarpDenyList.get();
        if (!dimensionAllowList.isEmpty() && !dimensionAllowList.contains(dimension.toString())) {
            return false;
        } else if (!dimensionDenyList.isEmpty() && dimensionDenyList.contains(dimension.toString())) {
            return false;
        }

        DimensionalWarp dimensionalWarpMode = WaystonesConfig.SERVER.dimensionalWarp.get();
        return dimensionalWarpMode == DimensionalWarp.ALLOW || dimensionalWarpMode == DimensionalWarp.GLOBAL_ONLY && waystone.isGlobal();
    }

    private static ItemStack findWarpItem(PlayerEntity player, WarpMode warpMode) {
        switch (warpMode) {
            case WARP_SCROLL:
                return findWarpItem(player, ModItems.warpScroll);
            case WARP_STONE:
                return findWarpItem(player, ModItems.warpStone);
            case RETURN_SCROLL:
                return findWarpItem(player, ModItems.returnScroll);
            case BOUND_SCROLL:
                return findWarpItem(player, ModItems.boundScroll);
            default:
                return ItemStack.EMPTY;
        }
    }

    private static ItemStack findWarpItem(PlayerEntity player, Item warpItem) {
        if (player.getHeldItemMainhand().getItem() == warpItem) {
            return player.getHeldItemMainhand();
        } else if (player.getHeldItemOffhand().getItem() == warpItem) {
            return player.getHeldItemOffhand();
        } else {
            return ItemStack.EMPTY;
        }
    }

    private static List<MobEntity> findLeashedAnimals(PlayerEntity player) {
        return player.world.getEntitiesWithinAABB(MobEntity.class, new AxisAlignedBB(player.getPosition()).grow(10),
                e -> player.equals(e.getLeashHolder())
        );
    }

    private static void teleportToWaystone(ServerPlayerEntity player, IWaystone waystone, WaystoneTeleportContext context) {
        BlockPos sourcePos = player.getPosition();
        BlockPos pos = waystone.getPos();
        Direction direction = context.getDirection();
        ServerWorld targetWorld = context.getTargetWorld();
        BlockPos targetPos = pos.offset(direction);
        Vector3d targetPos3d = new Vector3d(targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5);

        player.teleport(targetWorld, targetPos3d.getX(), targetPos.getY(), targetPos3d.getZ(), direction.getHorizontalAngle(), player.rotationPitch);

        // Resync some things that Vanilla is missing:
        for (EffectInstance effectinstance : player.getActivePotionEffects()) {
            player.connection.sendPacket(new SPlayEntityEffectPacket(player.getEntityId(), effectinstance));
        }
        player.setExperienceLevel(player.experienceLevel);

        NetworkHandler.channel.send(PacketDistributor.TRACKING_CHUNK.with(() -> player.world.getChunkAt(sourcePos)), new TeleportEffectMessage(sourcePos));
        NetworkHandler.channel.send(PacketDistributor.TRACKING_CHUNK.with(() -> player.world.getChunkAt(targetPos)), new TeleportEffectMessage(targetPos));

        context.getLeashedEntities().forEach(mob -> {
            if (targetWorld == mob.world) mob.setPosition(targetPos3d.x, targetPos3d.y, targetPos3d.z);
            else mob.changeDimension(targetWorld, new WaystoneTeleporter(targetPos3d));
        });
    }

    public static void deactivateWaystone(PlayerEntity player, IWaystone waystone) {
        getPlayerWaystoneData(player.world).deactivateWaystone(player, waystone);
    }

    private static boolean canUseWarpMode(PlayerEntity player, WarpMode warpMode, ItemStack heldItem, @Nullable IWaystone fromWaystone) {
        switch (warpMode) {
            case INVENTORY_BUTTON:
                return PlayerWaystoneManager.canUseInventoryButton(player);
            case WARP_SCROLL:
                return !heldItem.isEmpty() && heldItem.getItem() == ModItems.warpScroll;
            case BOUND_SCROLL:
                return !heldItem.isEmpty() && heldItem.getItem() == ModItems.boundScroll;
            case RETURN_SCROLL:
                return !heldItem.isEmpty() && heldItem.getItem() == ModItems.returnScroll;
            case WARP_STONE:
                return !heldItem.isEmpty() && heldItem.getItem() == ModItems.warpStone && PlayerWaystoneManager.canUseWarpStone(player, heldItem);
            case WAYSTONE_TO_WAYSTONE:
                return WaystonesConfig.COMMON.allowWaystoneToWaystoneTeleport.get() && fromWaystone != null && fromWaystone.isValid();
        }

        return false;
    }

    public static long getWarpStoneCooldownUntil(PlayerEntity player) {
        return getPlayerWaystoneData(player.world).getWarpStoneCooldownUntil(player);
    }

    public static long getWarpStoneCooldownLeft(PlayerEntity player) {
        long cooldownUntil = getWarpStoneCooldownUntil(player);
        return Math.max(0, cooldownUntil - System.currentTimeMillis());
    }

    public static void setWarpStoneCooldownUntil(PlayerEntity player, long timeStamp) {
        getPlayerWaystoneData(player.world).setWarpStoneCooldownUntil(player, timeStamp);
    }

    public static long getInventoryButtonCooldownUntil(PlayerEntity player) {
        return getPlayerWaystoneData(player.world).getInventoryButtonCooldownUntil(player);
    }

    public static long getInventoryButtonCooldownLeft(PlayerEntity player) {
        long cooldownUntil = getInventoryButtonCooldownUntil(player);
        return Math.max(0, cooldownUntil - System.currentTimeMillis());
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

    public static void makeWaystoneGlobal(IWaystone waystone) {
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
