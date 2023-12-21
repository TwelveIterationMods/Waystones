package net.blay09.mods.waystones.block.entity;

import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.waystones.api.WaystoneTypes;
import net.blay09.mods.waystones.core.Waystone;
import net.blay09.mods.waystones.core.WaystonePermissionManager;
import net.blay09.mods.waystones.menu.ModMenus;
import net.blay09.mods.waystones.menu.WaystoneSettingsMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
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
        return new BalmMenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("container.waystones.landing_stone");
            }

            @Override
            public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
                final var error = WaystonePermissionManager.mayEditWaystone(player, player.level(), getWaystone());
                return new WaystoneSettingsMenu(windowId, getWaystone(), LandingStoneBlockEntity.this, dataAccess, inventory, error.isEmpty());
            }

            @Override
            public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                buf.writeBlockPos(worldPosition);
                Waystone.write(buf, getWaystone());
                final var error = WaystonePermissionManager.mayEditWaystone(player, player.level(), getWaystone());
                buf.writeBoolean(error.isEmpty());
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
