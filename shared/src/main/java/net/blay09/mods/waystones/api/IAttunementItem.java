package net.blay09.mods.waystones.api;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IAttunementItem {
    @Nullable
    IWaystone getWaystoneAttunedTo(@Nullable MinecraftServer server, @Nullable Player player, ItemStack itemStack);

    void setWaystoneAttunedTo(ItemStack itemStack, @Nullable IWaystone waystone);
}
