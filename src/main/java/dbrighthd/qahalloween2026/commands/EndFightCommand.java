package dbrighthd.qahalloween2026.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import dbrighthd.qahalloween2026.mainloop.EndFight;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class EndFightCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) -> {
                    dispatcher.register(
                            Commands.literal("setphase")
                                    .requires(source -> source.permissions().hasPermission(
                                            net.minecraft.server.permissions.Permissions.COMMANDS_GAMEMASTER
                                    ))
                                    .then(Commands.argument("phase", StringArgumentType.word())
                                            .executes(context -> {
                                                String action = StringArgumentType.getString(context, "phase");

                                                EndFight.setPhase(EndFight.EndFightPhase.valueOf(action.toUpperCase()));
                                                return 1;
                                            })
                                    )
                    );
                }
        );
    }
}
