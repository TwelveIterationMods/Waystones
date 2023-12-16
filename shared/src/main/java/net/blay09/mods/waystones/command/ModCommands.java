package net.blay09.mods.waystones.command;

import net.blay09.mods.balm.api.command.BalmCommands;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.comparator.WaystoneComparators;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.argument;

public class ModCommands {
    public static void initialize(BalmCommands commands) {
        commands.register(dispatcher -> dispatcher.register(Commands.literal("waystones")
                .requires(source -> source.isPlayer() && source.hasPermission(2))
                .then(Commands.literal("count")
                        .then(argument("player", EntityArgument.player()).executes(new CountWaystonesCommand())))
                .then(Commands.literal("list")
                        .then(argument("player", EntityArgument.player()).executes(ctx -> {
                                    final var caller = ctx.getSource().getPlayerOrException();
                                    final var target = ctx.getArgument("player", EntitySelector.class).findSinglePlayer(ctx.getSource());
                                    final var waystones = PlayerWaystoneManager.getActivatedWaystones(target)
                                            .stream()
                                            .filter(it -> it.isOwner(target))
                                            .sorted(WaystoneComparators.forAdminInspection(caller, target))
                                            .toList();
                                    ctx.getSource().sendSystemMessage(Component.translatable("commands.waystones.list.header", target.getScoreboardName()));
                                    for (var waystone : waystones) {
                                        ctx.getSource().sendSystemMessage(componentForWaystoneList(caller, target, waystone));
                                    }
                                    final var result = Component.translatable("commands.waystones.list.footer", waystones.size());
                                    ctx.getSource().sendSuccess(() -> result, false);
                                    return waystones.size();
                                })
                                .then(Commands.literal("all").executes(ctx -> {
                                    final var caller = ctx.getSource().getPlayerOrException();
                                    final var target = ctx.getArgument("player", EntitySelector.class).findSinglePlayer(ctx.getSource());
                                    final var waystones = PlayerWaystoneManager.getActivatedWaystones(target)
                                            .stream()
                                            .sorted(WaystoneComparators.forAdminInspection(caller, target))
                                            .toList();
                                    ctx.getSource().sendSystemMessage(Component.translatable("commands.waystones.list.all.header", target.getScoreboardName()));
                                    for (var waystone : waystones) {
                                        ctx.getSource().sendSystemMessage(componentForWaystoneList(caller, target, waystone));
                                    }
                                    final var ownedCount = waystones.stream().filter(it -> it.isOwner(target)).count();
                                    final var result = Component.translatable("commands.waystones.list.all.footer", waystones.size(), ownedCount);
                                    ctx.getSource().sendSuccess(() -> result, false);
                                    return waystones.size();
                                }))))
                .then(Commands.literal("gui")
                        .then(argument("player", EntityArgument.player()).executes(new OpenPlayerWaystonesGuiCommand())))
        ));
    }

    private static Component componentForWaystoneList(ServerPlayer caller, ServerPlayer target, IWaystone waystone) {
        final var waystoneDimensionId = waystone.getDimension().location();
        final var waystonePos = waystone.getPos();
        Component location;
        if (waystone.getDimension() != caller.level().dimension()) {
            location = Component.translatable("commands.waystones.list.in_dimension", waystoneDimensionId);
        } else {
            final var distance = (int) caller.position().distanceTo(waystonePos.getCenter());
            location = Component.translatable("commands.waystones.list.at_distance", distance);
        }

        final var suggestedCommand = String.format("/execute in %s run teleport %d %d %d",
                waystoneDimensionId,
                waystonePos.getX(),
                waystonePos.getY(),
                waystonePos.getZ());

        final var coordinates = Component.translatable("commands.waystones.list.coordinates", waystonePos.getX(), waystonePos.getY(), waystonePos.getZ())
                .withStyle(ChatFormatting.YELLOW)
                .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggestedCommand)));

        final var waystoneName = Component.literal(waystone.getName())
                .withStyle(ChatFormatting.GREEN)
                .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggestedCommand)));

        final var entryKey = waystone.isOwner(target) ? "commands.waystones.list.entry.owned" : "commands.waystones.list.entry.activated";
        return Component.translatable(entryKey, location, coordinates, waystoneName);
    }
}
