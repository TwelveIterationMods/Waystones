package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.core.InMemoryPlayerWaystoneData;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.Waystone;
import net.blay09.mods.waystones.item.WarpStoneItem;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class MessageWaystones {

    private final List<IWaystone> waystones;
    private final long lastFreeWarp;
    private final long lastWarpStoneUse;

    public MessageWaystones(List<IWaystone> waystones, long lastFreeWarp, long lastWarpStoneUse) {
        this.waystones = waystones;
        this.lastFreeWarp = lastFreeWarp;
        this.lastWarpStoneUse = lastWarpStoneUse;
    }

    public static void encode(MessageWaystones message, PacketBuffer buf) {
        buf.writeShort(message.waystones.size());
        for (IWaystone waystone : message.waystones) {
            buf.writeUniqueId(waystone.getWaystoneUid());
            buf.writeString(waystone.getName());
            buf.writeBoolean(waystone.isGlobal());
            buf.writeInt(waystone.getDimensionType().getId());
            buf.writeBlockPos(waystone.getPos());
        }
        buf.writeLong(message.lastFreeWarp);
        buf.writeLong(Math.max(0, WaystoneConfig.SERVER.warpStoneCooldown.get() * 1000 - (System.currentTimeMillis() - message.lastWarpStoneUse)));
    }

    public static MessageWaystones decode(PacketBuffer buf) {
        int waystoneCount = buf.readShort();
        List<IWaystone> waystones = new ArrayList<>();
        for (int i = 0; i < waystoneCount; i++) {
            UUID waystoneUid = buf.readUniqueId();
            String name = buf.readString();
            boolean isGlobal = buf.readBoolean();
            DimensionType dimensionType = DimensionType.getById(buf.readInt());
            if (dimensionType == null) {
                dimensionType = DimensionType.OVERWORLD;
            }
            BlockPos pos = buf.readBlockPos();

            Waystone waystone = new Waystone(waystoneUid, dimensionType, pos, false, null);
            waystone.setName(name);
            waystone.setGlobal(isGlobal);
            waystones.add(waystone);
        }
        long lastFreeWarp = buf.readLong();
        long lastWarpStoneUse = buf.readLong();
        return new MessageWaystones(waystones, lastFreeWarp, lastWarpStoneUse);
    }

    public static void handle(MessageWaystones message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            InMemoryPlayerWaystoneData playerWaystoneData = (InMemoryPlayerWaystoneData) PlayerWaystoneManager.getPlayerWaystoneData(LogicalSide.CLIENT);
            playerWaystoneData.setWaystones(message.waystones);
            WarpStoneItem.lastTimerUpdate = System.currentTimeMillis();
        });
        context.setPacketHandled(true);
    }
}
