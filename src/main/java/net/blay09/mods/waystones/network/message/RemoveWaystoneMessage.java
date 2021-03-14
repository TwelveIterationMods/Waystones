package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.block.WaystoneBlock;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.Waystone;
import net.blay09.mods.waystones.core.WaystoneManager;
import net.blay09.mods.waystones.core.WaystoneProxy;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class RemoveWaystoneMessage {

    private final IWaystone waystone;

    public RemoveWaystoneMessage(IWaystone waystone) {
        this.waystone = waystone;
    }

    public static void encode(RemoveWaystoneMessage message, PacketBuffer buf) {
        buf.writeUniqueId(message.waystone.getWaystoneUid());
    }

    public static RemoveWaystoneMessage decode(PacketBuffer buf) {
        IWaystone waystone = new WaystoneProxy(buf.readUniqueId());
        return new RemoveWaystoneMessage(waystone);
    }

    public static void handle(RemoveWaystoneMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayerEntity player = context.getSender();
            if (player == null) {
                return;
            }

            PlayerWaystoneManager.deactivateWaystone(player, message.waystone);

            // If the waystone is global and the player is in creative mode, remove the global-ness
            if (message.waystone.isGlobal() && player.abilities.isCreativeMode && message.waystone instanceof WaystoneProxy) {
                IWaystone backingWaystone = ((WaystoneProxy) message.waystone).getBackingWaystone();
                if (backingWaystone instanceof Waystone) {
                    ((Waystone) backingWaystone).setGlobal(false);

                    // Check if the waystone block still exists - if not, completely remove the waystone from existence to remove it from all players
                    // This way we can't have orphan global waystones left over. And just in case the waystone *was* just being silk-touch moved, it's easy to reactivate a global waystone for everyone (since it does that automatically).
                    ServerWorld targetWorld = Objects.requireNonNull(player.world.getServer()).getWorld(backingWaystone.getDimension());
                    BlockPos pos = backingWaystone.getPos();
                    BlockState state = targetWorld != null ? targetWorld.getBlockState(pos) : null;
                    if (targetWorld == null || !(state.getBlock() instanceof WaystoneBlock)) {
                        WaystoneManager.get().removeWaystone(backingWaystone);
                        PlayerWaystoneManager.removeKnownWaystone(backingWaystone);
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }

}
