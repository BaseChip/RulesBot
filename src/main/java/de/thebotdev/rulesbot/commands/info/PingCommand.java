package de.thebotdev.rulesbot.commands.info;

import de.thebotdev.rulesbot.util.commandlib.CommandDescription;
import de.thebotdev.rulesbot.util.commandlib.Context;
import de.thebotdev.rulesbot.util.commandlib.RBCommand;
import net.dv8tion.jda.core.EmbedBuilder;

import static de.thebotdev.rulesbot.Main.shardManager;

@CommandDescription(
        name = "Ping", triggers = {"ping"},
        longDescription = "Just to see our current ping", description = "See the bots ping",
        usage = {"ping"}
)

public class PingCommand extends RBCommand {
    public void execute(Context ctx) {
        ctx.send(new EmbedBuilder()
                .setDescription(String.format("My Ping is `%.2f` ms.", shardManager.getAveragePing()))
                .build()).queue();
    }
}
