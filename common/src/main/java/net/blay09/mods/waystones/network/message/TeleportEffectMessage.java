package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.Waystones;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class TeleportEffectMessage implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<TeleportEffectMessage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Waystones.MOD_ID,
            "teleport_effect"));

    private final BlockPos pos;

    public TeleportEffectMessage(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(FriendlyByteBuf buf, TeleportEffectMessage message) {
        buf.writeBlockPos(message.pos);
    }

    public static TeleportEffectMessage decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        return new TeleportEffectMessage(pos);
    }

    public static void handle(Player player, TeleportEffectMessage message) {
        Level level = player.level();
        if (level != null) {
            for (int i = 0; i < 128; i++) {
                level.addParticle(ParticleTypes.PORTAL, message.pos.getX() + (level.random.nextDouble() - 0.5) * 3, message.pos.getY() + level.random.nextDouble() * 3, message.pos.getZ() + (level.random.nextDouble() - 0.5) * 3, (level.random.nextDouble() - 0.5) * 2, -level.random.nextDouble(), (level.random.nextDouble() - 0.5) * 2);
            }
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
