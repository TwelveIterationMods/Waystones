package net.blay09.mods.waystones.item;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ReturnScrollItem extends WarpScrollItem {

    public ReturnScrollItem(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable IWaystone getWaystoneAttunedTo(MinecraftServer server, Player player, ItemStack itemStack) {
        return PlayerWaystoneManager.getNearestWaystone(player);
    }
}
