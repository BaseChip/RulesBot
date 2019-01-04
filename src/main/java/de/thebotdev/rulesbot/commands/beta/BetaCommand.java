package de.thebotdev.rulesbot.commands.beta;

import de.thebotdev.rulesbot.util.commandlib.CommandDescription;
import de.thebotdev.rulesbot.util.commandlib.Context;
import de.thebotdev.rulesbot.util.commandlib.RBCommand;
import net.dv8tion.jda.core.EmbedBuilder;

@CommandDescription(
        name = "Beta", triggers = {"beta", "beta-info"},
        description = "Infos on how to become beta", usage = {"beta"},
        longDescription = "You want to test out some new functions how are not available for everyone? Then you should check out this command to see how to become a beta and which command you can use then ^^"
)
public class BetaCommand extends RBCommand {
    public void execute(Context ctx) {
        ctx.send(new EmbedBuilder()
                .setDescription("Hey,\nwe are glad that you are interested in our beta functions. There are two ways to get" +
                        " access to our beta functions. The first one is of course our" +
                        " [premium program](https://patreon.com/TheBotDev) and the other" +
                        " one is free of charge and can be used when you  voted us up on the" +
                        " [Discord Bot List (DBL)](https://discordbots.org/bot/rulesbot/vote)." +
                        " If you do this you will automatically get the beta role and full access to these features" +
                        " which are listed below. Please note that you have to be on our" +
                        " [support server](https://discord.gg/HD7x2vx), otherwise" +
                        " the bot will send you a message with instructions. You will only keep" +
                        " this access for 12 hours, but if you upvote the bot again you will get the role back.\n" +
                        "__**Functions in the beta:**__" +
                        "\n•report -> report users" +
                        "\n•setup_report -> setup a report channel where the bot sends if a user with more then 5" +
                        " reports join your server and accepts the rules\n" +
                        "•setup_channel [mention the channels with should be changed]-> let the bot setup the right" +
                        " channel permissions for you")
                .build()
        ).queue();
    }
}
