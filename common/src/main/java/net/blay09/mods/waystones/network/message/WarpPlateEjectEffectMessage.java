package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.Waystones;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class WarpPlateEjectEffectMessage implements CustomPacketPayload {

    public static final Type<WarpPlateEjectEffectMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Waystones.MOD_ID,
            "warp_plate_eject_effect"));

    private final BlockPos pos;

    public WarpPlateEjectEffectMessage(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(FriendlyByteBuf buf, WarpPlateEjectEffectMessage message) {
        buf.writeBlockPos(message.pos);
    }

    public static WarpPlateEjectEffectMessage decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        return new WarpPlateEjectEffectMessage(pos);
    }

    public static void handle(Player player, WarpPlateEjectEffectMessage message) {
        Level level = player.level();
        if (level != null) {
            for (int i = 0; i < 10; i++) {
                level.addParticle(ParticleTypes.SMALL_GUST, message.pos.getX() + 0.5 + (level.random.nextDouble() - 0.5), message.pos.getY() + level.random.nextDouble(), message.pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5), (level.random.nextDouble() - 0.5) * 2, -level.random.nextDouble(), (level.random.nextDouble() - 0.5) * 2);
                // TODO sound
            }
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
