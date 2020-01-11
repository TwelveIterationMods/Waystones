package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.api.WaystoneActivatedEvent;
import net.blay09.mods.waystones.config.DimensionalWarp;
import net.blay09.mods.waystones.config.WaystoneConfig;
import net.blay09.mods.waystones.item.ModItems;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.TeleportEffectMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.List;

public class PlayerWaystoneManager {

    private static IPlayerWaystoneData persistentPlayerWaystoneData = new PersistentPlayerWaystoneData();
    private static IPlayerWaystoneData inMemoryPlayerWaystoneData = new InMemoryPlayerWaystoneData();

    public static boolean mayBreakWaystone(PlayerEntity player, IBlockReader world, BlockPos pos) {
        if (WaystoneConfig.SERVER.restrictToCreative.get() && !player.abilities.isCreativeMode) {
            return false;
        }

        IWaystone waystone = WaystoneManager.get().getWaystoneAt(world, pos).orElseThrow(IllegalStateException::new);
        if (!player.abilities.isCreativeMode) {
            if (waystone.wasGenerated() && WaystoneConfig.SERVER.generatedWaystonesUnbreakable.get()) {
                return false;
            }

            return !waystone.isGlobal() || WaystoneConfig.SERVER.globalWaystoneRequiresCreative.get();
        }

        return true;
    }

    public static boolean mayPlaceWaystone(@Nullable PlayerEntity player) {
        return !WaystoneConfig.SERVER.restrictToCreative.get() || (player != null && player.abilities.isCreativeMode);
    }

    public static WaystoneEditPermissions mayEditWaystone(PlayerEntity player, World world, IWaystone waystone) {
        if (WaystoneConfig.SERVER.restrictToCreative.get() && !player.abilities.isCreativeMode) {
            return WaystoneEditPermissions.NOT_CREATIVE;
        }

        if (WaystoneConfig.SERVER.restrictRenameToOwner.get() && !waystone.isOwner(player)) {
            return WaystoneEditPermissions.NOT_THE_OWNER;
        }

        if (waystone.isGlobal() && !player.abilities.isCreativeMode && !WaystoneConfig.SERVER.globalWaystoneRequiresCreative.get()) {
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
        if (waystone.getDimensionType() != player.world.getDimension().getType()) {
            return WaystoneConfig.SERVER.dimensionalWarpXpCost.get();
        }

        double xpCostMultiplier = warpMode.getXpCostMultiplier();
        boolean enableXPCost = !player.abilities.isCreativeMode;
        if (waystone.isGlobal()) {
            xpCostMultiplier *= WaystoneConfig.SERVER.globalWaystoneXpCostMultiplier.get();
        }

        BlockPos pos = waystone.getPos();
        double dist = Math.sqrt(player.getDistanceSq(pos.getX(), pos.getY(), pos.getZ()));
        double xpLevelCost = WaystoneConfig.SERVER.blocksPerXPLevel.get() > 0 ? MathHelper.clamp(dist / (float) WaystoneConfig.SERVER.blocksPerXPLevel.get(), 0, WaystoneConfig.SERVER.maximumXpCost.get()) : 0;
        return enableXPCost ? (int) Math.round(xpLevelCost * xpCostMultiplier) : 0;
    }


    public static boolean canUseInventoryButton(PlayerEntity player) {
        return System.currentTimeMillis() - getPlayerWaystoneData(player.world).getLastInventoryWarp(player) > WaystoneConfig.SERVER.inventoryButtonCooldown.get() * 1000;
    }

    public static boolean canUseWarpStone(PlayerEntity player, ItemStack heldItem) {
        return System.currentTimeMillis() - getPlayerWaystoneData(player.world).getLastWarpStoneWarp(player) > WaystoneConfig.SERVER.warpStoneCooldown.get() * 1000;
    }

    public static double getCooldownMultiplier(IWaystone waystone) {
        return waystone.isGlobal() ? WaystoneConfig.SERVER.globalWaystoneCooldownMultiplier.get() : 1f;
    }

    public static boolean tryTeleportToWaystone(PlayerEntity player, IWaystone waystone, WarpMode warpMode, @Nullable IWaystone fromWaystone) {
        if (!waystone.isValid()) {
            return false;
        }

        ItemStack warpItem = findWarpItem(player, warpMode);
        if (!canUseWarpMode(player, warpMode, warpItem, fromWaystone)) {
            return false;
        }

        int xpLevelCost = getExperienceLevelCost(player, waystone, warpMode);
        if (player.experienceLevel < xpLevelCost) {
            return false;
        }

        DimensionalWarp dimensionalWarpMode = WaystoneConfig.SERVER.dimensionalWarp.get();
        boolean isDimensionalWarp = waystone.getDimensionType() != player.world.getDimension().getType();
        if (isDimensionalWarp && dimensionalWarpMode != DimensionalWarp.ALLOW && !(dimensionalWarpMode == DimensionalWarp.GLOBAL_ONLY && waystone.isGlobal())) {
            player.sendStatusMessage(new TranslationTextComponent("chat.waystones.cannot_dimension_warp"), false);
            return false;
        }

        if (warpMode == WarpMode.WARP_SCROLL) {
            warpItem.shrink(1);
        } else {
            if (warpMode == WarpMode.INVENTORY_BUTTON) {
                int cooldown = (int) (WaystoneConfig.SERVER.warpStoneCooldown.get() * getCooldownMultiplier(waystone));
                getPlayerWaystoneData(player.world).setInventoryButtonCooldownUntil(player, System.currentTimeMillis() + cooldown);
            } else if (warpMode == WarpMode.WARP_STONE) {
                int cooldown = (int) (WaystoneConfig.SERVER.warpStoneCooldown.get() * getCooldownMultiplier(waystone));
                getPlayerWaystoneData(player.world).setWarpStoneCooldownUntil(player, System.currentTimeMillis() + cooldown);
            }
        }

        if (xpLevelCost > 0) {
            player.addExperienceLevel(-xpLevelCost);
        }

        teleportToWaystone(player, waystone);
        return true;
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

    private static void teleportToWaystone(PlayerEntity player, IWaystone waystone) {
        BlockPos pos = waystone.getPos();
        player.setPositionAndUpdate(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        NetworkHandler.channel.send(PacketDistributor.TRACKING_CHUNK.with(() -> player.world.getChunkAt(pos)), new TeleportEffectMessage(pos));
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
                return fromWaystone != null && fromWaystone.isValid();
        }

        return false;
    }

    public static long getLastWarpStoneWarp(PlayerEntity player) {
        return getPlayerWaystoneData(player.world).getLastWarpStoneWarp(player);
    }

    public static void setLastWarpStoneWarp(PlayerEntity player, int timeStamp) {
        getPlayerWaystoneData(player.world).setWarpStoneCooldownUntil(player, timeStamp);
    }

    public static long getLastInventoryWarp(PlayerEntity player) {
        return getPlayerWaystoneData(player.world).getLastInventoryWarp(player);
    }

    public static void setInventoryButtonCooldownUntil(PlayerEntity player, long timeStamp) {
        getPlayerWaystoneData(player.world).setInventoryButtonCooldownUntil(player, timeStamp);
    }

    @Nullable
    public static IWaystone getNearestWaystone(PlayerEntity player) {
        return getPlayerWaystoneData(player.world).getWaystones(player).stream().min((first, second) -> {
            double firstDist = first.getPos().distanceSq(player.posX, player.posY, player.posZ, true);
            double secondDist = second.getPos().distanceSq(player.posX, player.posY, player.posZ, true);
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
        return player.abilities.isCreativeMode || WaystoneConfig.SERVER.globalWaystoneRequiresCreative.get();
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
            WaystoneSyncManager.sendWaystonesData(player);
        }
    }
}
