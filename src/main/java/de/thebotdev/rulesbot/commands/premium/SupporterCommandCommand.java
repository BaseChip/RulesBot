package de.thebotdev.rulesbot.commands.premium;

import de.thebotdev.rulesbot.util.commandlib.CommandDescription;
import de.thebotdev.rulesbot.util.commandlib.Context;
import de.thebotdev.rulesbot.util.commandlib.RBCommand;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;

import java.util.stream.Collectors;

import static de.thebotdev.rulesbot.Constants.DONATOR_ROLE;
import static de.thebotdev.rulesbot.Constants.THEBOTDEV_GUILD;
import static de.thebotdev.rulesbot.Main.shardManager;

@CommandDescription(
        name = "Supporter",
        triggers = {"supporter", "donators"},
        usage = {"supporter"},
        description = "Shows all supporters of the bot",
        longDescription = "Shows you all donators to the bot. Thank you guys so much."
)
public class SupporterCommandCommand extends RBCommand {
    public void execute(Context context) {
        Guild guild = shardManager.getGuildById(THEBOTDEV_GUILD);
        Role role = guild.getRoleById(DONATOR_ROLE);
        String donators = guild.getMembersWithRoles(role).stream()
                .map(member -> member.getUser().getName() + "#" + member.getUser().getDiscriminator())
                .collect(Collectors.joining("\n"));
        context.send(new EmbedBuilder()
                .setTitle("Here are all the generous donators who donated to our bot")
                .setDescription("```\n" + donators + "```")
                .build()).queue();
    }
}
