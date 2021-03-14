package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.api.KnownWaystonesEvent;
import net.blay09.mods.waystones.core.InMemoryPlayerWaystoneData;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.Waystone;
import net.blay09.mods.waystones.core.WaystoneManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class PlayerKnownWaystonesMessage {

    private final List<IWaystone> waystones;

    public PlayerKnownWaystonesMessage(List<IWaystone> waystones) {
        this.waystones = waystones;
    }

    public static void encode(PlayerKnownWaystonesMessage message, PacketBuffer buf) {
        buf.writeShort(message.waystones.size());
        for (IWaystone waystone : message.waystones) {
            Waystone.write(buf, waystone);
        }
    }

    public static PlayerKnownWaystonesMessage decode(PacketBuffer buf) {
        int waystoneCount = buf.readShort();
        List<IWaystone> waystones = new ArrayList<>();
        for (int i = 0; i < waystoneCount; i++) {
            waystones.add(Waystone.read(buf));
        }
        return new PlayerKnownWaystonesMessage(waystones);
    }

    public static void handle(PlayerKnownWaystonesMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            InMemoryPlayerWaystoneData playerWaystoneData = (InMemoryPlayerWaystoneData) PlayerWaystoneManager.getPlayerWaystoneData(LogicalSide.CLIENT);
            playerWaystoneData.setWaystones(message.waystones);
            for (IWaystone waystone : message.waystones) {
                WaystoneManager.get().updateWaystone(waystone);
            }
            MinecraftForge.EVENT_BUS.post(new KnownWaystonesEvent(message.waystones));
        });
        context.setPacketHandled(true);
    }
}
