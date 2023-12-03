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
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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

        Map<Boolean, List<IWaystone>> all = ownedOrActivatedByDistance(player, op);
        List<IWaystone> owned = all.get(true);
        List<IWaystone> others = all.get(false);

        String headerPart;
        String footerPart;
        if (this.listNotOwned) {
            headerPart = " (including not owned):";
            footerPart = "total, " + owned.size() + " owned";
        } else {
            others = Collections.emptyList();
            headerPart = ":";
            footerPart = "owned";
        }

        ctx.getSource().sendSystemMessage(Component.literal("----"));
        ctx.getSource().sendSystemMessage(Component.literal("Player ").append(player.getScoreboardName())
                .append(" waystones (x y z) coordinates").append(headerPart));
        sendWaystoneList(ctx.getSource(), op, owned, true);
        if (this.listNotOwned) {
            sendWaystoneList(ctx.getSource(), op, others, false);
        }

        int total = (owned.size() + others.size());
        ctx.getSource().sendSuccess(() -> Component.literal(total + " waystones " + footerPart), false);
        ctx.getSource().sendSystemMessage(Component.literal("----"));

        return total;
    }

    public static Map<Boolean, List<IWaystone>> ownedOrActivatedByDistance(Player target, Player commandOp) {
        Comparator<IWaystone> distanceComparator = createDistanceComparator(commandOp);

        Map<Boolean, List<IWaystone>> ownedAndActivated = PlayerWaystoneManager.getWaystones(target)
                .stream()
                //we only mark as owned the waystones that are truly bound to the target player's uuid
                .collect(Collectors.groupingBy(w -> target.getGameProfile().getId().equals(w.getOwnerUid())));

        List<IWaystone> owned = ownedAndActivated.computeIfAbsent(true, k -> Collections.emptyList());
        List<IWaystone> activated = ownedAndActivated.computeIfAbsent(false, k -> Collections.emptyList());
        owned.sort(distanceComparator);
        activated.sort(distanceComparator);
        return ownedAndActivated;
    }

    public static Comparator<IWaystone> createDistanceComparator(final Player player) {
        return Comparator.comparingDouble(w -> {
            ServerLevel targetLevel = player.getServer().getLevel(w.getDimension());
            if (targetLevel == null || !w.isValidInLevel(targetLevel)) return Double.MAX_VALUE;
            if (!targetLevel.equals(player.level())) return Double.MAX_VALUE - 1;

            return player.position().distanceTo(w.getPos().getCenter());
        });
    }

    private void sendWaystoneList(CommandSourceStack source, ServerPlayer op, List<IWaystone> waystones, boolean owned) {
        final String ownedHeader;
        if (!owned) ownedHeader = "activated";
        else if (this.listNotOwned) ownedHeader = "    owned";
        else ownedHeader = "         ";
        waystones.forEach(w -> {
            MutableComponent c = Component.literal(" - ")
                    .append(ownedHeader);

            MutableComponent coordinates = Component.literal(w.getPos().toShortString()).withStyle(ChatFormatting.YELLOW);
            MutableComponent distance;

            if (w.getDimension() != op.level().dimension()) {
                distance = Component.literal(w.getDimension().location().getPath())
                        .withStyle(ChatFormatting.ITALIC, ChatFormatting.YELLOW);
                c.append(" in ").append(distance).append(" at (").append(coordinates);
            } else {
                distance = Component.literal(String.valueOf((int) op.position().distanceTo(w.getPos().getCenter())))
                        .withStyle(ChatFormatting.BOLD);
                c.append(" at ").append(distance).append(" blocks away (").append(coordinates);
            }

            String suggestedCommand = "/execute in " + w.getDimension().location() + " run teleport " +
                    w.getPos().toShortString().replaceAll(",", "");
            c.append("): \"")
                    .append(Component.literal(w.getName())
                            .withStyle(ChatFormatting.GREEN, ChatFormatting.UNDERLINE)
                            .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggestedCommand))))
                    .append("\"");

            source.sendSystemMessage(c);
        });
    }
}
