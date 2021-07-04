package net.blay09.mods.waystones.network.message;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public class TeleportEffectMessage {

    private final BlockPos pos;

    public TeleportEffectMessage(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(TeleportEffectMessage message, FriendlyByteBuf buf) {
        buf.writeBlockPos(message.pos);
    }

    public static TeleportEffectMessage decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        return new TeleportEffectMessage(pos);
    }

    public static void handle(Player player, TeleportEffectMessage message) {
        if (player.level != null) {
            for (int i = 0; i < 128; i++) {
                player.level.addParticle(ParticleTypes.PORTAL, message.pos.getX() + (player.level.random.nextDouble() - 0.5) * 3, message.pos.getY() + player.level.random.nextDouble() * 3, message.pos.getZ() + (player.level.random.nextDouble() - 0.5) * 3, (player.level.random.nextDouble() - 0.5) * 2, -player.level.random.nextDouble(), (player.level.random.nextDouble() - 0.5) * 2);
            }
        }
    }
}
