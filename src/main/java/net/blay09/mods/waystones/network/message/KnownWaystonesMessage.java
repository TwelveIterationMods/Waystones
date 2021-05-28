package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.api.KnownWaystonesEvent;
import net.blay09.mods.waystones.core.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class KnownWaystonesMessage {

    private final ResourceLocation type;
    private final List<IWaystone> waystones;

    public KnownWaystonesMessage(ResourceLocation type, List<IWaystone> waystones) {
        this.type = type;
        this.waystones = waystones;
    }

    public static void encode(KnownWaystonesMessage message, PacketBuffer buf) {
        buf.writeResourceLocation(message.type);
        buf.writeShort(message.waystones.size());
        for (IWaystone waystone : message.waystones) {
            Waystone.write(buf, waystone);
        }
    }

    public static KnownWaystonesMessage decode(PacketBuffer buf) {
        ResourceLocation type = buf.readResourceLocation();
        int waystoneCount = buf.readShort();
        List<IWaystone> waystones = new ArrayList<>();
        for (int i = 0; i < waystoneCount; i++) {
            waystones.add(Waystone.read(buf));
        }
        return new KnownWaystonesMessage(type, waystones);
    }

    public static void handle(KnownWaystonesMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (message.type.equals(WaystoneTypes.WAYSTONE)) {
                InMemoryPlayerWaystoneData playerWaystoneData = (InMemoryPlayerWaystoneData) PlayerWaystoneManager.getPlayerWaystoneData(LogicalSide.CLIENT);
                playerWaystoneData.setWaystones(message.waystones);
                MinecraftForge.EVENT_BUS.post(new KnownWaystonesEvent(message.waystones));
            }

            for (IWaystone waystone : message.waystones) {
                WaystoneManager.get().updateWaystone(waystone);
            }
        });
        context.setPacketHandled(true);
    }
}
