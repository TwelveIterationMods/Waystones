package net.blay09.mods.waystones.network;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.network.message.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkHandler {

    private static final String version = "1.0";

    public static final SimpleChannel channel = NetworkRegistry.newSimpleChannel(new ResourceLocation(Waystones.MOD_ID, "network"), () -> version, it -> it.equals(version), it -> it.equals(version));

    public static void init() {
        channel.registerMessage(0, MessageWaystones.class, MessageWaystones::encode, MessageWaystones::decode, MessageWaystones::handle);
        channel.registerMessage(1, InventoryButtonMessage.class, InventoryButtonMessage::encode, InventoryButtonMessage::decode, InventoryButtonMessage::handle);
        channel.registerMessage(2, MessageEditWaystone.class, MessageEditWaystone::encode, MessageEditWaystone::decode, MessageEditWaystone::handle);
        channel.registerMessage(3, SelectWaystoneMessage.class, SelectWaystoneMessage::encode, SelectWaystoneMessage::decode, SelectWaystoneMessage::handle);
        channel.registerMessage(4, MessageTeleportEffect.class, MessageTeleportEffect::encode, MessageTeleportEffect::decode, MessageTeleportEffect::handle);
        channel.registerMessage(5, SortWaystoneMessage.class, SortWaystoneMessage::encode, SortWaystoneMessage::decode, SortWaystoneMessage::handle);
        channel.registerMessage(6, RemoveWaystoneMessage.class, RemoveWaystoneMessage::encode, RemoveWaystoneMessage::decode, RemoveWaystoneMessage::handle);
    }

    public static void sendTo(Object message, PlayerEntity player) {
        channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), message);
    }
}
