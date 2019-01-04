package de.thebotdev.rulesbot;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.thebotdev.rulesbot.util.RegisterListener;
import de.thebotdev.rulesbot.util.commandlib.AdvancedWaiter;
import de.thebotdev.rulesbot.util.commandlib.CommandListener;
import de.thebotdev.rulesbot.util.commandlib.CommandListenerBuilder;
import de.thebotdev.rulesbot.util.database.Database;
import de.thebotdev.rulesbot.util.database.Database_Stats;
import io.sentry.Sentry;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.ISnowflake;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.ExceptionEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.reflections.Reflections;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static org.slf4j.LoggerFactory.getLogger;


public class Main {
    public static Gson gson = new GsonBuilder()
            .create();
    public static Config config;
    public static ShardManager shardManager;
    public static Database database;
    public static Database_Stats stats_db;
    public static CommandListener commandListener;

    public static void main(String[] args) {
        initLogging();
        setupSentry();
        loadConfig();
        initDatabase();
        addCommands();
        initShards();
    }

    private static void initLogging() {
        ((Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(Level.ERROR);

    }

    private static void setupSentry() {
        Sentry.init();
    }

    private static void initDatabase() {
        database = new Database(config.getDatabase());
        stats_db = new Database_Stats(config.getDatabase());
    }

    private static void initShards() {
        try {
            DefaultShardManagerBuilder builder = new DefaultShardManagerBuilder()
                    .setShardsTotal(-1)
                    .setGame(Game.playing("-help | sponsored by dyn-box.de"))
                    .setToken(config.getToken())
                    .addEventListeners(commandListener)
                    .addEventListeners(AdvancedWaiter.getEventWaiter())
                    .addEventListeners(new ListenerAdapter() {
                        @Override
                        public void onException(ExceptionEvent event) {
                            Sentry.capture(event.getCause());
                        }
                    });
            for (Class<?> clazz : new Reflections("de.thebotdev.rulesbot.commands.listeners").getTypesAnnotatedWith(RegisterListener.class)) {
                builder.addEventListeners(clazz.newInstance());
            }
            shardManager = builder.build();
        } catch (LoginException e) {
            getLogger("login").error("Failure during login", e);
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    private static void addCommands() {
        commandListener = new CommandListenerBuilder()
                .setPrefix(message -> Stream.of(message)
                        .map(Message::getGuild)
                        .filter(Objects::nonNull)
                        .map(ISnowflake::getId)
                        .map(database::getPrefix)
                        .findAny()
                        .orElse(Optional.empty())
                        .orElse("-"))
                .setAllowMentionPrefix(true)
                .build();
        commandListener.findCommands("de.thebotdev.rulesbot.commands.info");
        commandListener.findCommands("de.thebotdev.rulesbot.commands.premium");
        commandListener.findCommands("de.thebotdev.rulesbot.commands.beta");
        commandListener.findCommands("de.thebotdev.rulesbot.commands.guild");
        commandListener.findCommands("de.thebotdev.rulesbot.commands.admin");
        commandListener.findCommands("de.thebotdev.rulesbot.commands.listeners");
    }

    private static void loadConfig() {
        try {
            config = gson.fromJson(new FileReader("config/config.json"), Config.class);
        } catch (FileNotFoundException e) {
            getLogger("startup").error("Config not found.", e);
        }
    }
}
