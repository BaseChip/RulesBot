package de.thebotdev.rulesbot.commands.listeners;

import de.thebotdev.rulesbot.util.RegisterListener;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;

import static de.thebotdev.rulesbot.Main.shardManager;

@RegisterListener
public class GuildRemove extends ListenerAdapter {
    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        User owner = event.getGuild().getOwner().getUser();
        Guild tbd = shardManager.getGuildById(385848724628439062L);
        Channel chan = tbd.getTextChannelById(437581969652580362L);
        ((TextChannel) chan).sendMessage(new EmbedBuilder()
                // 525689227732713482
                .setColor(Color.red)
                .setDescription(String.format("%s Server left: %s\n" +
                        "ID: %s\n" +
                        "Owner: %s\n" +
                        "Users: %s\n" +
                        "Servercount now: %s", shardManager.getEmoteById(525689269323563018L).getAsMention(), event.getGuild().getName(), event.getGuild().getId(), owner.getName() + "#" + owner.getDiscriminator(), event.getGuild().getMembers().size(), shardManager.getGuilds().size()))
                .build()).queue();

    }
}
