package de.thebotdev.rulesbot.commands.listeners;

import de.thebotdev.rulesbot.util.RegisterListener;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import static de.thebotdev.rulesbot.Main.database;
import static de.thebotdev.rulesbot.Main.shardManager;

@RegisterListener
public class MentionListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().equals(event.getJDA().getSelfUser().getAsMention())) {
            String prefix = database.getPrefix(event.getGuild().getId()).orElse("-");
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setColor(0x36393f)
                    .setDescription(String.format("%s**Hey!** My prefix is `%s`, but you can change it with the `pchange` command ;) To get more information about my commands please execute -help or -help <command> to get more information about one command.\nFor more help please visit our website (https://thebotdev.de) or join our support server (http://support.thebotdev.de)", shardManager.getEmoteById(513715339725373440L).getAsMention(), prefix))
                    .build()).queue();
        }
    }
}
