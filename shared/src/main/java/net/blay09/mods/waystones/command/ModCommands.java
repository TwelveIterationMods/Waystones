package net.blay09.mods.waystones.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.blay09.mods.balm.api.command.BalmCommands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;

import static net.minecraft.commands.Commands.argument;

public class ModCommands {
    public static void initialize(BalmCommands commands) {
        commands.register(dispatcher -> dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("waystones")
                .requires(source -> source.isPlayer() && source.hasPermission(2))
                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("count")
                        .then(argument("player", EntityArgument.player()).executes(new CountWaystonesCommand())))
                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("owned")
                        .then(argument("player", EntityArgument.player()).executes(new ListWaystonesCommand(false))))
                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("activated")
                        .then(argument("player", EntityArgument.player()).executes(new ListWaystonesCommand(true))))
                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("gui")
                        .then(argument("player", EntityArgument.player()).executes(new OpenPlayerWaystonesGuiCommand())))
        ));
    }
}
