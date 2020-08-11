package net.blay09.mods.waystones.network.message;

import com.electronwill.nightconfig.toml.TomlFormat;
import net.blay09.mods.waystones.config.WaystoneConfig;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.io.ByteArrayInputStream;
import java.util.function.Supplier;

public class SyncConfigMessage {

    private final byte[] configData;

    public SyncConfigMessage(final byte[] configFileData) {
        this.configData = configFileData;
    }

    public static void encode(SyncConfigMessage message, PacketBuffer buf) {
        buf.writeByteArray(message.configData);
    }

    public static SyncConfigMessage decode(PacketBuffer buf) {
        return new SyncConfigMessage(buf.readByteArray());
    }

    public static void handle(SyncConfigMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (context.getDirection() != NetworkDirection.PLAY_TO_CLIENT) {
                return;
            }

            WaystoneConfig.serverSpec.setConfig(TomlFormat.instance().createParser().parse(new ByteArrayInputStream(message.configData)));
        });
        context.setPacketHandled(true);
    }
}
