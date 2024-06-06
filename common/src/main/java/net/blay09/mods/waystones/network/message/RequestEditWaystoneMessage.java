package net.blay09.mods.waystones.network.message;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class RequestEditWaystoneMessage implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<RequestEditWaystoneMessage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Waystones.MOD_ID,
            "request_edit_waystone"));

    private final BlockPos pos;

    public RequestEditWaystoneMessage(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(FriendlyByteBuf buf, RequestEditWaystoneMessage message) {
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

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

