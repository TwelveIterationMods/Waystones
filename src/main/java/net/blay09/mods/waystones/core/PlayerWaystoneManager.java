package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.api.WaystoneActivatedEvent;
import net.blay09.mods.waystones.item.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;

public class PlayerWaystoneManager {

    public static boolean mayBreakWaystone(PlayerEntity player, IBlockReader world, BlockPos pos) {
        if (WaystoneConfig.SERVER.creativeModeOnly.get() && !player.abilities.isCreativeMode) {
            return false;
        }

        IWaystone waystone = WaystoneManager.getWaystoneAt(world, pos).orElseThrow(IllegalStateException::new);
        if (!player.abilities.isCreativeMode) {
            if (waystone.wasGenerated() && WaystoneConfig.COMMON.disallowBreakingGenerated.get()) {
                return false;
            }

            return !waystone.isGlobal() || WaystoneConfig.SERVER.allowEveryoneGlobal.get();
        }

        return true;
    }

    public static boolean mayPlaceWaystone(@Nullable PlayerEntity player) {
        return !WaystoneConfig.SERVER.creativeModeOnly.get() || (player != null && player.abilities.isCreativeMode);
    }

    public static WaystoneEditPermissions mayEditWaystone(PlayerEntity player, World world, BlockPos pos, IWaystone waystone) {
        if (WaystoneConfig.SERVER.creativeModeOnly.get() && !player.abilities.isCreativeMode) {
            return WaystoneEditPermissions.NOT_CREATIVE;
        }

        if (WaystoneConfig.SERVER.restrictRenameToOwner.get() && !waystone.isOwner(player)) {
            return WaystoneEditPermissions.NOT_THE_OWNER;
        }

        if (waystone.isGlobal() && !player.abilities.isCreativeMode && !WaystoneConfig.SERVER.allowEveryoneGlobal.get()) {
            return WaystoneEditPermissions.GET_CREATIVE;
        }

        return WaystoneEditPermissions.ALLOW;
    }

    public static boolean isWaystoneActivated(PlayerEntity player, IWaystone waystone) {
        return PlayerWaystoneData.isWaystoneActivated(player, waystone);
    }

    public static void activateWaystone(PlayerEntity player, IWaystone waystone) {
        PlayerWaystoneData.activateWaystone(player, waystone);

        StringTextComponent nameComponent = new StringTextComponent(waystone.getName());
        nameComponent.getStyle().setColor(TextFormatting.WHITE);
        TranslationTextComponent chatComponent = new TranslationTextComponent("waystones:activatedWaystone", nameComponent);
        chatComponent.getStyle().setColor(TextFormatting.YELLOW);
        player.sendMessage(chatComponent);

        MinecraftForge.EVENT_BUS.post(new WaystoneActivatedEvent(waystone));
    }

    public static int getExperienceLevelCost(PlayerEntity player, IWaystone waystone, WarpMode warpMode) {
        boolean enableXPCost = WaystoneConfig.SERVER.globalWaystonesCostXp.get() || !waystone.isGlobal();
        if (warpMode == WarpMode.INVENTORY_BUTTON) {
            enableXPCost = enableXPCost && WaystoneConfig.COMMON.inventoryButtonXpCost.get() && !player.abilities.isCreativeMode;
        } else if (warpMode == WarpMode.WARP_STONE) {
            enableXPCost = enableXPCost && WaystoneConfig.COMMON.warpStoneXpCost.get() && !player.abilities.isCreativeMode;
        } else if (warpMode == WarpMode.WAYSTONE) {
            enableXPCost = enableXPCost && WaystoneConfig.COMMON.waystoneXpCost.get() && !player.abilities.isCreativeMode;
        }

        BlockPos pos = waystone.getPos();
        int dist = (int) Math.sqrt(player.getDistanceSq(pos.getX(), pos.getY(), pos.getZ()));
        int xpLevelCost = WaystoneConfig.SERVER.blocksPerXPLevel.get() > 0 ? MathHelper.clamp(dist / WaystoneConfig.SERVER.blocksPerXPLevel.get(), 0, WaystoneConfig.SERVER.maximumXpCost.get()) : 0;
        return enableXPCost ? xpLevelCost : 0;
    }


    public static boolean canUseInventoryButton(PlayerEntity player) {
        return System.currentTimeMillis() - PlayerWaystoneData.getLastInventoryWarp(player) > WaystoneConfig.SERVER.teleportButtonCooldown.get() * 1000;
    }

    public static boolean canUseWarpStone(PlayerEntity player, ItemStack heldItem) {
        return System.currentTimeMillis() - PlayerWaystoneData.getLastWarpStoneWarp(player) > WaystoneConfig.SERVER.warpStoneCooldown.get() * 1000;
    }

    public static boolean shouldTriggerCooldown(IWaystone waystone) {
        return !waystone.isGlobal() || !WaystoneConfig.COMMON.globalNoCooldown.get();
    }

    public static boolean tryTeleportToWaystone(ServerPlayerEntity player, IWaystone waystone, WarpMode warpMode, ItemStack warpItem, @Nullable IWaystone fromWaystone) {
        if (!canUseWarpMode(player, warpMode, warpItem, fromWaystone)) {
            return false;
        }

        int xpLevelCost = PlayerWaystoneManager.getExperienceLevelCost(player, waystone, warpMode);
        if (player.experienceLevel < xpLevelCost) {
            return false;
        }

        if (warpMode == WarpMode.WARP_SCROLL) {
            warpItem.shrink(1);
        } else if (shouldTriggerCooldown(waystone)) {
            if (warpMode == WarpMode.INVENTORY_BUTTON) {
                PlayerWaystoneData.setLastInventoryWarp(player, System.currentTimeMillis());
            } else if (warpMode == WarpMode.WARP_STONE) {
                PlayerWaystoneData.setLastWarpStoneWarp(player, System.currentTimeMillis());
            }
        }

        if (xpLevelCost > 0) {
            player.addExperienceLevel(-xpLevelCost);
        }

        teleportToWaystone(player, waystone);
        return true;
    }

    private static void teleportToWaystone(ServerPlayerEntity player, IWaystone waystone) {
        player.sendMessage(new StringTextComponent("*teleports behind you*")); // TODO
    }

    public static void deactivateWaystone(ServerPlayerEntity player, IWaystone entry) {
        PlayerWaystoneData.deactivateWaystone(player, entry);
    }

    private static boolean canUseWarpMode(PlayerEntity player, WarpMode warpMode, ItemStack heldItem, @Nullable IWaystone fromWaystone) {
        switch (warpMode) {
            case INVENTORY_BUTTON:
                return PlayerWaystoneManager.canUseInventoryButton(player);
            case WARP_SCROLL:
                return !heldItem.isEmpty() && heldItem.getItem() == ModItems.warpScroll;
            case WARP_STONE:
                return !heldItem.isEmpty() && heldItem.getItem() == ModItems.warpstone && PlayerWaystoneManager.canUseWarpStone(player, heldItem);
            case WAYSTONE:
                return fromWaystone != null && fromWaystone.isValid();
        }

        return false;
    }
}
