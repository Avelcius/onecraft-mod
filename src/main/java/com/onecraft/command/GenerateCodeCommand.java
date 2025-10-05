package com.onecraft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.onecraft.state.CodeState;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public class GenerateCodeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        dispatcher.register(CommandManager.literal("onecraft")
                .then(CommandManager.literal("generatecode")
                        .requires(source -> source.hasPermissionLevel(2)) // Require op level 2
                        .then(CommandManager.argument("code_id", StringArgumentType.word())
                                .executes(GenerateCodeCommand::run))));
    }

    private static int run(CommandContext<ServerCommandSource> context) {
        String codeId = StringArgumentType.getString(context, "code_id");
        ServerWorld world = context.getSource().getWorld();
        CodeState state = CodeState.getServerState(world);

        if (state.generateCode(codeId)) {
            context.getSource().sendFeedback(() -> Text.literal("New code generated for ID: " + codeId), true);
        } else {
            context.getSource().sendFeedback(() -> Text.literal("Code with ID '" + codeId + "' already exists. No new code was generated."), false);
        }

        return 1;
    }
}