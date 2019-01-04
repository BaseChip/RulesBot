package de.thebotdev.rulesbot.commands.guild;

import de.thebotdev.rulesbot.util.commandlib.CommandDescription;
import de.thebotdev.rulesbot.util.commandlib.Context;
import de.thebotdev.rulesbot.util.commandlib.RBCommand;
import net.dv8tion.jda.core.EmbedBuilder;

import static de.thebotdev.rulesbot.Main.database;

@CommandDescription(
        name = "Pchange", triggers = {"pchange", "prefix"},
        description = "Change your prefix", usage = {"pchange [new prefix]", "prefix [new prefix]"},
        longDescription = "You can change your guilds bot prefix which you use for any command to execute"
)
public class PchangeCommand extends RBCommand {
    public void execute(Context ctx, String new_prefix) {
        String prefix = database.getPrefix(ctx.getEvent().getGuild().getId()).orElse("-");
        database.setPrefix(ctx.getGuild().getId(), new_prefix);
        ctx.send(new EmbedBuilder()
                .appendDescription(String.format("Changed the prefix from `%s` to `%s`", prefix, new_prefix))
                .build()).queue();
    }
}