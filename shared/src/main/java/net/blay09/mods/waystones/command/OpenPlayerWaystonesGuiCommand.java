package net.blay09.mods.waystones.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.waystones.api.TeleportFlags;
import net.blay09.mods.waystones.comparator.WaystoneComparators;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.Waystone;
import net.blay09.mods.waystones.menu.ModMenus;
import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.Collections;
import java.util.Set;

public class OpenPlayerWaystonesGuiCommand implements Command<CommandSourceStack> {
    @Override
    public int run(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer target = ctx.getArgument("player", EntitySelector.class).findSinglePlayer(ctx.getSource());
        ServerPlayer op = ctx.getSource().getPlayerOrException();
        final var waystones = PlayerWaystoneManager.getActivatedWaystones(target).stream().sorted(WaystoneComparators.forAdminInspection(op, target)).toList();
        BalmMenuProvider menuProvider = new BalmMenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("container.waystones.waystone_admin_selection", target.getScoreboardName());
            }

            @Override
            public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
                return new WaystoneSelectionMenu(ModMenus.adminSelection.get(), null, windowId, waystones, Set.of(TeleportFlags.ADMIN));
            }

            @Override
            public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                Waystone.writeList(buf, waystones);
            }
        };
        Balm.getNetworking().openGui(op, menuProvider);

        return 0;
    }
}
