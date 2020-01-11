package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.config.WaystoneConfig;
import net.blay09.mods.waystones.core.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class EditWaystoneMessage {

    private final IWaystone waystone;
    private final String name;
    private final boolean isGlobal;

    public EditWaystoneMessage(IWaystone waystone, String name, boolean isGlobal) {
        this.waystone = waystone;
        this.name = name;
        this.isGlobal = isGlobal;
    }

    public static void encode(EditWaystoneMessage message, PacketBuffer buf) {
        buf.writeUniqueId(message.waystone.getWaystoneUid());
        buf.writeString(message.name);
        buf.writeBoolean(message.isGlobal);
    }

    public static EditWaystoneMessage decode(PacketBuffer buf) {
        IWaystone waystone = new WaystoneProxy(buf.readUniqueId());
        String name = buf.readString();
        boolean isGlobal = buf.readBoolean();
        return new EditWaystoneMessage(waystone, name, isGlobal);
    }

    public static void handle(EditWaystoneMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayerEntity player = context.getSender();
            if (player == null) {
                return;
            }

            WaystoneEditPermissions permissions = PlayerWaystoneManager.mayEditWaystone(player, player.world, message.waystone);
            if (permissions != WaystoneEditPermissions.ALLOW) {
                return;
            }

            BlockPos pos = message.waystone.getPos();
            if (player.getDistanceSq(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f) > 64) {
                return;
            }

            Waystone backingWaystone = (Waystone) ((WaystoneProxy) message.waystone).getBackingWaystone();
            String legalName = makeNameLegal(message.name);
            backingWaystone.setName(legalName);

            if (PlayerWaystoneManager.mayEditGlobalWaystones(player)) {
                if (!backingWaystone.isGlobal() && message.isGlobal) {
                    PlayerWaystoneManager.makeWaystoneGlobal(backingWaystone);
                }
                backingWaystone.setGlobal(message.isGlobal);
            }

            WaystoneManager.get().markDirty();
            WaystoneSyncManager.sendWaystonesDataToAll();

            player.closeScreen();
        });
        context.setPacketHandled(true);
    }

    private static String makeNameLegal(String name) {
        String inventoryButtonMode = WaystoneConfig.SERVER.inventoryButton.get();
        if (inventoryButtonMode.equals(name)) {
            return name + "*";
        }

        return name;
    }
}
