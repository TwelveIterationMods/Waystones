package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.Waystones;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class TeleportEffectMessage {

    private final BlockPos pos;

    public TeleportEffectMessage(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(TeleportEffectMessage message, PacketBuffer buf) {
        buf.writeBlockPos(message.pos);
    }

    public static TeleportEffectMessage decode(PacketBuffer buf) {
        BlockPos pos = buf.readBlockPos();
        return new TeleportEffectMessage(pos);
    }

    public static void handle(TeleportEffectMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            // Hard-limit the distance since the attenuation doesn't seem to work well enough for greater distances
            if (mc.player != null && mc.player.getPosition().withinDistance(message.pos, 64)) {
                Waystones.proxy.playSound(SoundEvents.BLOCK_PORTAL_TRAVEL, message.pos, 1f);
            }

            if (mc.world != null) {
                for (int i = 0; i < 128; i++) {
                    mc.world.addParticle(ParticleTypes.PORTAL, message.pos.getX() + (mc.world.rand.nextDouble() - 0.5) * 3, message.pos.getY() + mc.world.rand.nextDouble() * 3, message.pos.getZ() + (mc.world.rand.nextDouble() - 0.5) * 3, (mc.world.rand.nextDouble() - 0.5) * 2, -mc.world.rand.nextDouble(), (mc.world.rand.nextDouble() - 0.5) * 2);
                }
            }
        });
        context.setPacketHandled(true);
    }
}
