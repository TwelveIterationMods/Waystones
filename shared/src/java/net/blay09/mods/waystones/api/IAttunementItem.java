package net.blay09.mods.waystones.api;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface IAttunementItem {
    @Nullable
    IWaystone getWaystoneAttunedTo(@Nullable MinecraftServer server, ItemStack itemStack);
}
