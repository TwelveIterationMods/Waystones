package net.blay09.mods.waystones.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.stream.Collectors;

public class ListWaystonesCommand implements Command<CommandSourceStack> {

    private final boolean listNotOwned;

    public ListWaystonesCommand(boolean listNotOwned) {
        this.listNotOwned = listNotOwned;
    }


    @Override
    public int run(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getArgument("player", EntitySelector.class).findSinglePlayer(ctx.getSource());
        ServerPlayer op = ctx.getSource().getPlayerOrException();

        final var sortedWaystones = ownedOrActivatedByDistance(player, op);

        final var headerKey = listNotOwned ? "commands.waystones.list.activated.header" : "commands.waystones.list.owned.header";
        ctx.getSource().sendSystemMessage(Component.translatable(headerKey, player.getScoreboardName()));

        CommandSourceStack commandSourceStack = ctx.getSource();
        for (IWaystone waystone : sortedWaystones.get(WaystoneOwnership.OWNED)) {
            commandSourceStack.sendSystemMessage(componentForWaystone(ctx.getSource(), WaystoneOwnership.OWNED, waystone));
        }
        if (listNotOwned) {
            for (IWaystone waystone : sortedWaystones.get(WaystoneOwnership.ACTIVATED)) {
                commandSourceStack.sendSystemMessage(componentForWaystone(ctx.getSource(), WaystoneOwnership.ACTIVATED, waystone));
            }
        }

        int totalCount = sortedWaystones.size();
        int ownedCount = sortedWaystones.get(WaystoneOwnership.OWNED).size();

        if (listNotOwned) {
            ctx.getSource().sendSuccess(() -> Component.translatable("commands.waystones.list.activated.footer", totalCount, ownedCount), false);
            return totalCount;
        } else {
            ctx.getSource().sendSuccess(() -> Component.translatable("commands.waystones.list.owned.footer", ownedCount), false);
            return ownedCount;
        }
    }

    public static Map<WaystoneOwnership, List<IWaystone>> ownedOrActivatedByDistance(Player target, Player commandOp) {
        Comparator<IWaystone> distanceComparator = createDistanceComparator(commandOp);

        EnumMap<WaystoneOwnership, List<IWaystone>> ownedAndActivated = PlayerWaystoneManager.getWaystones(target)
                .stream()
                //we only mark as owned the waystones that are truly bound to the target player's uuid
                .collect(Collectors.groupingBy(
                        w -> target.getGameProfile().getId().equals(w.getOwnerUid()) ? WaystoneOwnership.OWNED : WaystoneOwnership.ACTIVATED,
                        () -> new EnumMap<>(WaystoneOwnership.class),
                        Collectors.toList())
                );

        List<IWaystone> owned = ownedAndActivated.computeIfAbsent(WaystoneOwnership.OWNED, k -> Collections.emptyList());
        List<IWaystone> activated = ownedAndActivated.computeIfAbsent(WaystoneOwnership.ACTIVATED, k -> Collections.emptyList());
        owned.sort(distanceComparator);
        activated.sort(distanceComparator);
        return ownedAndActivated;
    }

    public static Comparator<IWaystone> createDistanceComparator(final Player player) {
        return Comparator.comparingDouble(w -> {
            ResourceKey<Level> targetDimension = w.getDimension();
            if (targetDimension == null) return Double.MAX_VALUE;
            if (!targetDimension.equals(player.level().dimension())) return Double.MAX_VALUE - 1;

            return player.position().distanceTo(w.getPos().getCenter());
        });
    }

    private Component componentForWaystone(CommandSourceStack source, WaystoneOwnership ownership, IWaystone waystone) throws CommandSyntaxException {
        final var op = source.getPlayerOrException();

        final var waystoneDimensionId = waystone.getDimension().location();
        final var waystonePos = waystone.getPos();
        Component location;
        if(waystone.getDimension() != op.level().dimension()) {
            location = Component.translatable("commands.waystones.list.in_dimension", waystoneDimensionId);
        } else {
            final var distance = (int) op.position().distanceTo(waystonePos.getCenter());
            location = Component.translatable("commands.waystones.list.at_distance", distance);
        }

        final var suggestedCommand = String.format("/execute in %s run teleport %d %d %d", waystoneDimensionId, waystonePos.getX(), waystonePos.getY(), waystonePos.getZ());

        final var coordinates = Component.translatable("commands.waystones.list.coordinates", waystonePos.getX(), waystonePos.getY(), waystonePos.getZ())
                .withStyle(ChatFormatting.YELLOW)
                .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggestedCommand)));

        final var waystoneName = Component.literal(waystone.getName())
                .withStyle(ChatFormatting.GREEN)
                .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggestedCommand)));

        final var entryKey = switch (ownership) {
            case OWNED -> "commands.waystones.list.owned.entry";
            case ACTIVATED -> "commands.waystones.list.activated.entry";
        };
        return Component.translatable(entryKey, location, coordinates, waystoneName);
    }
}
