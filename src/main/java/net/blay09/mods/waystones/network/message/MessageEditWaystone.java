package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.GlobalWaystones;
import net.blay09.mods.waystones.WarpMode;
import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.tileentity.WaystoneTileEntity;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

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

            if (WaystoneConfig.SERVER.creativeModeOnly.get() && !player.abilities.isCreativeMode) {
                return;
            }

            World world = player.getEntityWorld();
            BlockPos pos = message.pos;
            if (player.getDistanceSq(pos.getX(), pos.getY(), pos.getZ()) > 100) {
                return;
            }

            GlobalWaystones globalWaystones = GlobalWaystones.get(player.world);
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof WaystoneTileEntity) {
                WaystoneTileEntity tileWaystone = ((WaystoneTileEntity) tileEntity).getParent();
                if (globalWaystones.getGlobalWaystone(tileWaystone.getWaystoneName()) != null && !player.abilities.isCreativeMode && !WaystoneConfig.SERVER.allowEveryoneGlobal.get()) {
                    return;
                }

                if (WaystoneConfig.SERVER.restrictRenameToOwner.get() && !tileWaystone.isOwner(player)) {
                    player.sendMessage(new TranslationTextComponent("waystones:notTheOwner"));
                    return;
                }

                String newName = message.name;
                // Disallow %RANDOM% for non-creative players to prevent unbreakable waystone exploit
                if (newName.equals("%RANDOM%") && !player.abilities.isCreativeMode) {
                    newName = "RANDOM";
                }

                if (globalWaystones.getGlobalWaystone(newName) != null && !player.abilities.isCreativeMode) {
                    player.sendMessage(new TranslationTextComponent("waystones:nameOccupied", newName));
                    return;
                }

                WaystoneEntry oldWaystone = new WaystoneEntry(tileWaystone);
                if (oldWaystone.isGlobal()) {
                    globalWaystones.removeGlobalWaystone(oldWaystone);
                }

                tileWaystone.setWaystoneName(newName);

                WaystoneEntry newWaystone = new WaystoneEntry(tileWaystone);
                if (message.isGlobal && (player.abilities.isCreativeMode || WaystoneConfig.SERVER.allowEveryoneGlobal.get())) {
                    tileWaystone.setGlobal(true);
                    newWaystone.setGlobal(true);
                    globalWaystones.addGlobalWaystone(newWaystone);
                    for (PlayerEntity otherPlayer : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                        WaystoneManager.sendPlayerWaystones(otherPlayer);
                    }
                }

                if (!newWaystone.isGlobal()) {
                    WaystoneManager.removePlayerWaystone(player, oldWaystone);
                    WaystoneManager.addPlayerWaystone(player, newWaystone);
                    WaystoneManager.sendPlayerWaystones(player);
                }

                if (message.fromSelectionGui) {
                    NetworkHandler.channel.send(PacketDistributor.PLAYER.with(() -> player), new MessageOpenWaystoneSelection(WarpMode.WAYSTONE, Hand.MAIN_HAND, newWaystone));
                }
            }
        });
    }
}
