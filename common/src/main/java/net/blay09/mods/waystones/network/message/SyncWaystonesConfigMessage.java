package net.blay09.mods.waystones.network.message;

import net.blay09.mods.balm.api.network.SyncConfigMessage;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.config.WaystonesConfigData;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class SyncWaystonesConfigMessage extends SyncConfigMessage<WaystonesConfigData> {

    public static final CustomPacketPayload.Type<SyncWaystonesConfigMessage> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(Waystones.MOD_ID, "sync_config"));

    public SyncWaystonesConfigMessage(WaystonesConfigData data) {
        super(TYPE, data);
    }
}
