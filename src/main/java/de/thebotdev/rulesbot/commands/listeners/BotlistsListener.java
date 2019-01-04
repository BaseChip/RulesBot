package de.thebotdev.rulesbot.commands.listeners;

import de.thebotdev.rulesbot.BotlistsConfig;
import de.thebotdev.rulesbot.util.FutureUtil;
import de.thebotdev.rulesbot.util.RegisterListener;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.discordbots.api.client.DiscordBotListAPI;

import java.util.concurrent.CompletableFuture;

import static de.thebotdev.rulesbot.Main.config;
import static de.thebotdev.rulesbot.Main.shardManager;
import static org.slf4j.LoggerFactory.getLogger;

@RegisterListener
public class BotlistsListener extends ListenerAdapter {
    public static DiscordBotListAPI api;

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        if (!loadApi()) return;
        getLogger("botlists").info(event.getGuild().getName());
        api.setStats(0, shardManager.getShardsTotal(), shardManager.getGuilds().size());
    }

    public static boolean loadApi() {
        BotlistsConfig listconfig = config.getBotlists();
        if (api == null) {
            if (listconfig.getDblToken().isEmpty())
                return false;
            synchronized (BotlistsListener.class) {
                if (api == null) {
                    api = new DiscordBotListAPI.Builder()
                            .token(listconfig.getDblToken())
                            .botId(shardManager.getApplicationInfo().complete().getId())
                            .build();
                }
            }
        }
        return true;
    }

    public static CompletableFuture<Boolean> hasVoted(User u) {
        if (!loadApi())
            return FutureUtil.resolve(false);
        return api.hasVoted(u.getId()).toCompletableFuture();
    }

    // STR + O
    // STR + SHIFT + ALT + L
}
