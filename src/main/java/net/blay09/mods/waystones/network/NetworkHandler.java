package net.blay09.mods.waystones.network;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.network.message.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkHandler {

    private static final String version = "1.0";

    public static final SimpleChannel channel = NetworkRegistry.newSimpleChannel(new ResourceLocation(Waystones.MOD_ID, "network"), () -> version, it -> it.equals(version), it -> it.equals(version));

    public static void init() {
        channel.registerMessage(0, MessageWaystones.class, MessageWaystones::encode, MessageWaystones::decode, MessageWaystones::handle);
        channel.registerMessage(1, MessageFreeWarpReturn.class, MessageFreeWarpReturn::encode, MessageFreeWarpReturn::decode, MessageFreeWarpReturn::handle);
        channel.registerMessage(2, MessageEditWaystone.class, MessageEditWaystone::encode, MessageEditWaystone::decode, MessageEditWaystone::handle);
        channel.registerMessage(3, MessageTeleportToWaystone.class, MessageTeleportToWaystone::encode, MessageTeleportToWaystone::decode, MessageTeleportToWaystone::handle);
        channel.registerMessage(4, MessageTeleportEffect.class, MessageTeleportEffect::encode, MessageTeleportEffect::decode, MessageTeleportEffect::handle);
        channel.registerMessage(5, MessageSortWaystone.class, MessageSortWaystone::encode, MessageSortWaystone::decode, MessageSortWaystone::handle);
        channel.registerMessage(6, MessageRemoveWaystone.class, MessageRemoveWaystone::encode, MessageRemoveWaystone::decode, MessageRemoveWaystone::handle);
        channel.registerMessage(7, MessageTeleportToGlobal.class, MessageTeleportToGlobal::encode, MessageTeleportToGlobal::decode, MessageTeleportToGlobal::handle);
        channel.registerMessage(8, MessageOpenWaystoneSelection.class, MessageOpenWaystoneSelection::encode, MessageOpenWaystoneSelection::decode, MessageOpenWaystoneSelection::handle);
    }

    public static void sendTo(Object message, PlayerEntity player) {
        // TODO
    }
}
