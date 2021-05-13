package net.blay09.mods.waystones.container;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.block.SharestoneBlock;
import net.blay09.mods.waystones.core.*;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.DyeColor;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class WaystoneSelectionContainer extends Container {

    private final WarpMode warpMode;
    private final IWaystone fromWaystone;
    private final List<IWaystone> waystones;

    public WaystoneSelectionContainer(ContainerType<WaystoneSelectionContainer> type, WarpMode warpMode, @Nullable IWaystone fromWaystone, int windowId, List<IWaystone> waystones) {
        super(type, windowId);
        this.warpMode = warpMode;
        this.fromWaystone = fromWaystone;
        this.waystones = waystones;
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        if (fromWaystone != null) {
            BlockPos pos = fromWaystone.getPos();
            return player.getDistanceSq((double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5) <= 64;
        }

        return true;
    }

    @Nullable
    public IWaystone getWaystoneFrom() {
        return fromWaystone;
    }

    public WarpMode getWarpMode() {
        return warpMode;
    }

    public List<IWaystone> getWaystones() {
        return waystones;
    }

    public static WaystoneSelectionContainer createWaystoneSelection(int windowId, PlayerEntity player, WarpMode warpMode, @Nullable IWaystone fromWaystone) {
        List<IWaystone> waystones = PlayerWaystoneManager.getWaystones(player);
        return new WaystoneSelectionContainer(ModContainers.waystoneSelection, warpMode, fromWaystone, windowId, waystones);
    }

    public static WaystoneSelectionContainer createSharestoneSelection(int windowId, IWaystone fromWaystone, BlockState state) {
        SharestoneBlock block = (SharestoneBlock) state.getBlock();
        ResourceLocation waystoneType = WaystoneTypes.getSharestone(block.getColor());
        List<IWaystone> waystones = WaystoneManager.get().getWaystonesByType(waystoneType).collect(Collectors.toList());
        return new WaystoneSelectionContainer(ModContainers.sharestoneSelection, WarpMode.SHARESTONE_TO_SHARESTONE, fromWaystone, windowId, waystones);
    }

    public static void writeSharestoneContainer(PacketBuffer buf, BlockPos pos, @Nullable DyeColor color) {
        ResourceLocation waystoneType = WaystoneTypes.getSharestone(color);
        List<IWaystone> waystones = WaystoneManager.get().getWaystonesByType(waystoneType).collect(Collectors.toList());

        buf.writeBlockPos(pos);
        buf.writeShort(waystones.size());
        for (IWaystone waystone : waystones) {
            Waystone.write(buf, waystone);
        }
    }
}
