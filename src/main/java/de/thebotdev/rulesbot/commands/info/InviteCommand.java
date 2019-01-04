package de.thebotdev.rulesbot.commands.info;

import de.thebotdev.rulesbot.util.commandlib.CommandDescription;
import de.thebotdev.rulesbot.util.commandlib.Context;
import de.thebotdev.rulesbot.util.commandlib.RBCommand;
import net.dv8tion.jda.core.EmbedBuilder;

@CommandDescription(
        name = "Invite", triggers = {"invite"},
        description = "get the bots invite", usage = {"invite"},
        longDescription = "get the bots invite"
)
public class InviteCommand extends RBCommand {
    public void execute(Context ctx) {
        ctx.send(new EmbedBuilder()
                .setDescription("Hey,\nto invite me you can use [this](https://discordapp.com/api/oauth2/authorize?client_id=389082834670845952&permissions=336030791&scope=bot \"Greg did nothing, for real\") link")
                .build()).queue();
    }
}
