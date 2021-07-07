package net.blay09.mods.waystones.network;

import net.blay09.mods.forbic.config.ForbicConfig;
import net.blay09.mods.forbic.network.ForbicNetworking;
import net.blay09.mods.forbic.network.SyncConfigMessage;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.config.WaystonesConfigData;
import net.blay09.mods.waystones.network.message.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ModNetworking {

    public static void initialize() {
        ForbicNetworking.registerServerboundPacket(id("inventory_button"), InventoryButtonMessage.class, InventoryButtonMessage::encode, InventoryButtonMessage::decode, InventoryButtonMessage::handle);
        ForbicNetworking.registerServerboundPacket(id("edit_waystone"), EditWaystoneMessage.class, EditWaystoneMessage::encode, EditWaystoneMessage::decode, EditWaystoneMessage::handle);
        ForbicNetworking.registerServerboundPacket(id("select_waystone"), SelectWaystoneMessage.class, SelectWaystoneMessage::encode, SelectWaystoneMessage::decode, SelectWaystoneMessage::handle);
        ForbicNetworking.registerServerboundPacket(id("sort_waystone"), SortWaystoneMessage.class, SortWaystoneMessage::encode, SortWaystoneMessage::decode, SortWaystoneMessage::handle);
        ForbicNetworking.registerServerboundPacket(id("remove_waystone"), RemoveWaystoneMessage.class, RemoveWaystoneMessage::encode, RemoveWaystoneMessage::decode, RemoveWaystoneMessage::handle);
        ForbicNetworking.registerServerboundPacket(id("request_edit_waystone"), RequestEditWaystoneMessage.class, RequestEditWaystoneMessage::encode, RequestEditWaystoneMessage::decode, RequestEditWaystoneMessage::handle);

        ForbicNetworking.registerClientboundPacket(id("known_waystones"), KnownWaystonesMessage.class, KnownWaystonesMessage::encode, KnownWaystonesMessage::decode, KnownWaystonesMessage::handle);
        ForbicNetworking.registerClientboundPacket(id("teleport_effect"), TeleportEffectMessage.class, TeleportEffectMessage::encode, TeleportEffectMessage::decode, TeleportEffectMessage::handle);
        ForbicNetworking.registerClientboundPacket(id("waystone_cooldown"), PlayerWaystoneCooldownsMessage.class, PlayerWaystoneCooldownsMessage::encode, PlayerWaystoneCooldownsMessage::decode, PlayerWaystoneCooldownsMessage::handle);
        ForbicNetworking.registerClientboundPacket(id("sync_config"), SyncWaystonesConfigMessage.class,
                SyncConfigMessage.createEncoder(WaystonesConfigData.class),
                SyncConfigMessage.createDecoder(WaystonesConfigData.class, SyncWaystonesConfigMessage::new,
                        SyncConfigMessage.createDeepCopyFactory(() -> ForbicConfig.getConfig(WaystonesConfigData.class), WaystonesConfigData::new)),
                WaystonesConfig::handleSync);
    }

    @NotNull
    private static ResourceLocation id(String name) {
        return new ResourceLocation(Waystones.MOD_ID, name);
    }

}
