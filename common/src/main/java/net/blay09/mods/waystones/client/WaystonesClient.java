package net.blay09.mods.waystones.client;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.balm.api.event.client.UseItemInputEvent;
import net.blay09.mods.waystones.InternalClientMethodsImpl;
import net.blay09.mods.waystones.api.client.WaystonesClientAPI;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.blay09.mods.waystones.client.requirement.RequirementClientRegistry;
import net.blay09.mods.waystones.compat.Compat;
import net.blay09.mods.waystones.network.message.RequestEditWaystoneMessage;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Locale;

public class WaystonesClient {
    public static void initialize() {
        WaystonesClientAPI.__internalMethods = new InternalClientMethodsImpl();
        RequirementClientRegistry.registerDefaults();

        ModClientEventHandlers.initialize();
        ModRenderers.initialize(BalmClient.getRenderers());
        ModScreens.initialize(BalmClient.getScreens());
        ModTextures.initialize(BalmClient.getTextures());
        ModModels.initialize(BalmClient.getModels());

        InventoryButtonGuiHandler.initialize();

        Compat.isVivecraftInstalled = ClientBrandRetriever.getClientModName().toLowerCase(Locale.ENGLISH).contains(Compat.VIVECRAFT);

        /* TODO Balm.getEvents().onEvent(UseItemInputEvent.class, event -> {
            final var mc = Minecraft.getInstance();
            if (mc.level == null || mc.player == null || mc.hitResult == null || mc.hitResult.getType() != HitResult.Type.BLOCK) {
                return;
            }

            if (mc.player.isShiftKeyDown()) {
                final var blockHitResult = (BlockHitResult) mc.hitResult;
                final var targetBlockEntity = mc.level.getBlockEntity(blockHitResult.getBlockPos());
                if (targetBlockEntity instanceof WaystoneBlockEntityBase waystoneBlockEntity) {
                    Balm.getNetworking().sendToServer(new RequestEditWaystoneMessage(waystoneBlockEntity.getBlockPos()));
                    mc.player.swing(event.getHand());
                    event.setCanceled(true);
                }
            }
        });*/
    }
}
