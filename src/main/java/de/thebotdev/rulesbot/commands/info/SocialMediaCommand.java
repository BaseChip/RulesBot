package de.thebotdev.rulesbot.commands.info;

import de.thebotdev.rulesbot.util.commandlib.CommandDescription;
import de.thebotdev.rulesbot.util.commandlib.Context;
import de.thebotdev.rulesbot.util.commandlib.RBCommand;
import net.dv8tion.jda.core.EmbedBuilder;

@CommandDescription(
        name = "Social_Media", triggers = {"social_media", "SocialMedia", "twitter", "youtube"},
        description = "Here you can find the links to our social media sites", usage = {"SocialMediaCommand"},
        longDescription = "Twitter, Youtube and our Support server. All links in one place"
)
public class SocialMediaCommand extends RBCommand {
    public void execute(Context ctx) {
        ctx.send(new EmbedBuilder()
                .setDescription("Here you can find the links to all my social media platforms")
                .addField("Twitter", "https://twitter.com/TheBotDev", false)
                .addField("YouTube", "https://www.youtube.com/channel/UCf9HSCk2EmjqTsZRSCBzFog", false)
                .addField("Discord", "https://discord.gg/HD7x2vx", false)
                .build()).queue();
    }
}
