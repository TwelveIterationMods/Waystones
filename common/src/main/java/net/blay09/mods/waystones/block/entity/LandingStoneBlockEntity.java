package net.blay09.mods.waystones.block.entity;

import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.waystones.api.WaystoneTypes;
import net.blay09.mods.waystones.core.WaystonePermissionManager;
import net.blay09.mods.waystones.menu.WaystoneMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class LandingStoneBlockEntity extends WaystoneBlockEntityBase {

    public LandingStoneBlockEntity(BlockPos worldPosition, BlockState state) {
        super(ModBlockEntities.landingStone.get(), worldPosition, state);
    }

    @Override
    protected ResourceLocation getWaystoneType() {
        return WaystoneTypes.LANDING_STONE;
    }

    @Override
    public MenuProvider getMenuProvider() {
        return new BalmMenuProvider<WaystoneMenu.Data>() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("container.waystones.landing_stone");
            }

            @Override
            public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
                final var error = WaystonePermissionManager.mayEditWaystone(player, player.level(), getWaystone());
                return new WaystoneMenu(windowId, getWaystone(), LandingStoneBlockEntity.this, dataAccess, inventory, error.isEmpty());
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, WaystoneMenu.Data> getScreenStreamCodec() {
                return WaystoneMenu.STREAM_CODEC;
            }

            @Override
            public WaystoneMenu.Data getScreenOpeningData(ServerPlayer player) {
                final var error = WaystonePermissionManager.mayEditWaystone(player, player.level(), getWaystone());
                return new WaystoneMenu.Data(worldPosition, getWaystone(), error.isEmpty());
            }
        };
    }

    @Override
    public @Nullable MenuProvider getSettingsMenuProvider() {
        return getMenuProvider();
    }

    @Override
    public boolean shouldPerformInitialAttunement() {
        return true;
    }
}
