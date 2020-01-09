package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WaystoneEditPermissions;
import net.blay09.mods.waystones.core.WaystoneManager;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageEditWaystone {

    private final BlockPos pos;
    private final String name;
    private final boolean isGlobal;
    private final boolean fromSelectionGui;

    public MessageEditWaystone(BlockPos pos, String name, boolean isGlobal, boolean fromSelectionGui) {
        this.pos = pos;
        this.name = name;
        this.isGlobal = isGlobal;
        this.fromSelectionGui = fromSelectionGui;
    }

    public static void encode(MessageEditWaystone message, PacketBuffer buf) {
        buf.writeLong(message.pos.toLong());
        buf.writeString(message.name);
        buf.writeBoolean(message.isGlobal);
        buf.writeBoolean(message.fromSelectionGui);
    }

    public static MessageEditWaystone decode(PacketBuffer buf) {
        BlockPos pos = BlockPos.fromLong(buf.readLong());
        String name = buf.readString();
        boolean isGlobal = buf.readBoolean();
        boolean fromSelectionGui = buf.readBoolean();
        return new MessageEditWaystone(pos, name, isGlobal, fromSelectionGui);
    }

    public static void handle(MessageEditWaystone message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayerEntity player = context.getSender();
            if (player == null) {
                return;
            }

            IWaystone waystone = WaystoneManager.get().getWaystoneAt(player.world, message.pos).orElseThrow(IllegalStateException::new);
            WaystoneEditPermissions permissions = PlayerWaystoneManager.mayEditWaystone(player, player.world, message.pos, waystone);
            if (permissions != WaystoneEditPermissions.ALLOW) {
                return;
            }

            World world = player.getEntityWorld();
            BlockPos pos = message.pos;
            if (player.getDistanceSq(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f) > 64) {
                return;
            }

            String newName = message.name;

            // Disallow %RANDOM% for non-creative players to prevent unbreakable waystone exploit
            if (newName.equals("%RANDOM%") && !player.abilities.isCreativeMode) {
                newName = "RANDOM";
            }

            // TODO waystone.setWaystoneName(newName);

            if (message.isGlobal && (player.abilities.isCreativeMode || WaystoneConfig.SERVER.allowEveryoneGlobal.get())) {
                // TODO waystone.setGlobal(true);
            }

            if (message.fromSelectionGui) {
                // TODO    NetworkHandler.channel.send(PacketDistributor.PLAYER.with(() -> player), new MessageOpenWaystoneSelection(WarpMode.WAYSTONE_TO_WAYSTONE, Hand.MAIN_HAND, waystone));
            }
        });
        context.setPacketHandled(true);
    }
}
