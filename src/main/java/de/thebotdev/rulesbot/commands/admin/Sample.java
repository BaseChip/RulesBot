package de.thebotdev.rulesbot.commands.admin;

import de.thebotdev.rulesbot.util.commandlib.CommandDescription;
import de.thebotdev.rulesbot.util.commandlib.Context;
import de.thebotdev.rulesbot.util.commandlib.RBCommand;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;

@CommandDescription(
        name = "", triggers = {"sample"},
        description = "the short one", usage = {"sample"},
        longDescription = "The long one",
        hidden = true
)
public class Sample extends RBCommand {
    public void execute(Context ctx, User user) {
        ctx.send(new EmbedBuilder()
                .setColor(0x36393f)
                .setDescription("Test " + user.getName())
                .build()).queue();
    }
}
