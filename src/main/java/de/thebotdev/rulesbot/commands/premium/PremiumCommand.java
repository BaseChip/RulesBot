package de.thebotdev.rulesbot.commands.premium;

import de.thebotdev.rulesbot.util.commandlib.CommandDescription;
import de.thebotdev.rulesbot.util.commandlib.Context;
import de.thebotdev.rulesbot.util.commandlib.RBCommand;
import net.dv8tion.jda.core.EmbedBuilder;

@CommandDescription(
        name = "Premium", triggers = {"premium", "premium-info", "premium_info", "patreon", "donate"},
        description = "All infos you need if you think about going premium", usage = {"premium"},
        longDescription = "Shows you a lot of information about going premium."
)
public class PremiumCommand extends RBCommand {
    public void execute(Context ctx) {
        ctx.send(new EmbedBuilder()
                .setDescription("Hey, cool that you think about to go premium! If you go premium you would support the " +
                        "developer and the moderators. Also you would help us to cover our costs ;) But what " +
                        "would you get?\n__**What will you get?**__\n• **change footer text**\n• **change embed " +
                        "color**\n• Access to our **beta commands**\n• faster " +
                        "support\n• exclusive textchannels\n__**Where to buy?**__\nYou could buy it on Patreon [" +
                        "here](https://www.patreon.com/TheBotDev), but other then normally with patreon this is " +
                        "an **one time payment** so you dont need to pay monthly for staying premium!")
                .build()
        ).queue();
    }
}
