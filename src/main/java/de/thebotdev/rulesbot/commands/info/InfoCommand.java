package de.thebotdev.rulesbot.commands.info;

import de.thebotdev.rulesbot.util.commandlib.CommandDescription;
import de.thebotdev.rulesbot.util.commandlib.Context;
import de.thebotdev.rulesbot.util.commandlib.RBCommand;
import net.dv8tion.jda.core.EmbedBuilder;

@CommandDescription(
        name = "Info", triggers = {"info"},
        description = "get infos about the bot", usage = {"info"},
        longDescription = "Shows you infos"
)
public class InfoCommand extends RBCommand {
    public void execute(Context ctx) {
        ctx.send(new EmbedBuilder()
                .setColor(0x36393f)
                .setTitle("Bot by:")
                .setDescription("BaseChip#2390, romangraef89#0998, Rxsto#4224, Skidder#6775")
                .setAuthor("Bot Info", "https://thebotdev.de", "https://thebotdev.de/assets/img/Fragezeichen.png")
                .addField("Project", "TheBotDev", false)
                .addField("Logo/website designed by:", "tobimori#1135", false)
                .addField("Support server:", "https://discord.gg/HD7x2vx", false)
                .addField("Website:", "https://TheBotDev.de", false)
                .addField("Status:", "https://status.TheBotDev.de", false)
                .addField("Invite me to your server:", "http://support.thebotdev.de", false)
                .setFooter("Thanks for using our Bot! If you have any Problems feel free to join our support server", "https://thebotdev.de/img/bot_img.png")
                .build()).queue();
    }
}
