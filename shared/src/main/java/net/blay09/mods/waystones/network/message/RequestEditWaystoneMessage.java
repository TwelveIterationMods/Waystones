package net.blay09.mods.waystones.network.message;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class RequestEditWaystoneMessage {

    private final BlockPos pos;

    public RequestEditWaystoneMessage(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(RequestEditWaystoneMessage message, FriendlyByteBuf buf) {
        buf.writeBlockPos(message.pos);
    }

    public static RequestEditWaystoneMessage decode(FriendlyByteBuf buf) {
        final var pos = buf.readBlockPos();
        return new RequestEditWaystoneMessage(pos);
    }

    public static void handle(ServerPlayer player, RequestEditWaystoneMessage message) {
        final var pos = message.pos;
        if (player.distanceToSqr(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f) > 64) {
            return;
        }

        final var blockEntity = player.level().getBlockEntity(pos);
        if (blockEntity instanceof WaystoneBlockEntityBase waystoneBlockEntity) {
            final var menuProvider = waystoneBlockEntity.getSettingsMenuProvider();
            if (menuProvider != null) {
                Balm.getNetworking().openGui(player, menuProvider);
            }
        }
    }
}

