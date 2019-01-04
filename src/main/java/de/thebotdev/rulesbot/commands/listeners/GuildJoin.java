package de.thebotdev.rulesbot.commands.listeners;

import de.thebotdev.rulesbot.util.RegisterListener;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;

import static de.thebotdev.rulesbot.Main.shardManager;
import static org.slf4j.LoggerFactory.getLogger;

@RegisterListener
public class GuildJoin extends ListenerAdapter {
    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        getLogger("server join - message to the owner").info(event.getGuild().getName());
        User owner = event.getGuild().getOwner().getUser();
        String mes = String.format("Hey %s\nthanks for adding me to your server %s!\nMy prefix is `-`, but you can change it with the `pchange` command ;) To get more information about my commands please execute -help or -help <command> to get more information about one command.\nFor more help please visit our website (https://thebotdev.de) or join our support server (http://support.thebotdev.de)", shardManager.getEmoteById(513715339725373440L).getAsMention(), event.getGuild().getName());
        owner.openPrivateChannel().complete().sendMessage(mes).queue();
        Guild tbd = shardManager.getGuildById(385848724628439062L);
        Channel chan = tbd.getTextChannelById(437581969652580362L);
        ((TextChannel) chan).sendMessage(new EmbedBuilder()
                // 525689227732713482
                .setColor(Color.green)
                .setDescription(String.format("%s Server jointed: %s\n" +
                        "ID: %s\n" +
                        "Owner: %s\n" +
                        "Users: %s\n" +
                        "Servercount now: %s", shardManager.getEmoteById(525689227732713482L).getAsMention(), event.getGuild().getName(), event.getGuild().getId(), owner.getName() + "#" + owner.getDiscriminator(), event.getGuild().getMembers().size(), shardManager.getGuilds().size()))
                .build()).queue();

    }
}
