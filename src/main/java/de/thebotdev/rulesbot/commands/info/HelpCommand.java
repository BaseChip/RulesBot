package de.thebotdev.rulesbot.commands.info;

import de.thebotdev.rulesbot.util.commandlib.CommandDescription;
import de.thebotdev.rulesbot.util.commandlib.Context;
import de.thebotdev.rulesbot.util.commandlib.RBCommand;
import net.dv8tion.jda.core.EmbedBuilder;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@CommandDescription(
        name = "Help",
        triggers = {"help"},
        usage = {"help", "help <command>"},
        description = "Shows this message",
        longDescription = "This just shows you the help message"
)
public class HelpCommand extends RBCommand {


    public void execute(Context ctx, String... commands) {
        if (commands.length == 0) {
            String commandText;
            commandText = ctx.getCommandListener().getVisibleCommands()
                    .stream()
                    .map(command -> "**" + command.getName() + "**\n" + command.getShortDescription())
                    .collect(Collectors.joining("\n"));
            ctx.send(new EmbedBuilder()
                    .setTitle("Help")
                    .setDescription(commandText)
                    .setFooter("Bot by BaseChip#2390 | TheBotDev Project", "https://thebotdev.de/img/bot_img.png")
                    .build()).queue();
            return;
        }
        if (commands.length != 1) {
            ctx.send(new EmbedBuilder()
                    .setTitle("Please mention only one command.")
                    .build())
                    .queue();
            return;
        }
        Optional<RBCommand> cmd = ctx.getCommandListener().getVisibleCommands().stream()
                .filter(command -> command.isTrigger(commands[0]))
                .findAny();
        if (!cmd.isPresent()) {
            ctx.send(new EmbedBuilder()
                    .setDescription("Command not found.")
                    .build()).queue();
            return;
        }
        RBCommand command = cmd.get();
        ctx.send(new EmbedBuilder()
                .setTitle(command.getName())
                .addField("Usage", Arrays.stream(command.getDescription().usage()).map(x -> ctx.getPrefix() + x).collect(Collectors.joining("\n")), true)
                .setDescription(command.getLongDescription())
                .setFooter("[] = required | <> = optional", "https://thebotdev.de/img/bot_img.png")
                .build()).queue();
    }

}
