package de.thebotdev.rulesbot.commands.info;

import de.thebotdev.rulesbot.util.commandlib.CommandDescription;
import de.thebotdev.rulesbot.util.commandlib.Context;
import de.thebotdev.rulesbot.util.commandlib.RBCommand;
import net.dv8tion.jda.core.EmbedBuilder;

@CommandDescription(
        name = "Upcoming", triggers = {"upcoming", "soon"},
        description = "See the upcoming commands", usage = {"upcoming", "soon"},
        longDescription = "Get a list with all the commands we still need to develop in this rewrite"
)
public class UpcomingCommands extends RBCommand {
    public void execute(Context ctx) {
        ctx.send(new EmbedBuilder()
                .setColor(0x36393f)
                .setDescription("**We still work on this commands:**\n•report / setup_report\n•ticket system")
                .build()).queue();
    }
}
