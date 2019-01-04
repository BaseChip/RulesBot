package de.thebotdev.rulesbot.commands.info;

import com.sun.management.OperatingSystemMXBean;
import de.thebotdev.rulesbot.util.commandlib.CommandDescription;
import de.thebotdev.rulesbot.util.commandlib.Context;
import de.thebotdev.rulesbot.util.commandlib.RBCommand;
import net.dv8tion.jda.core.EmbedBuilder;

import java.lang.management.ManagementFactory;

import static de.thebotdev.rulesbot.Main.shardManager;

@CommandDescription(
        name = "Bot_Info", triggers = {"botinfo", "bot-info", "bot_info", "stats"},
        description = "Shows some information", usage = {"botinfo"},
        longDescription = "Botinfo"
)
public class BotInfoCommand extends RBCommand {
    public void execute(Context ctx) {
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        ctx.send(new EmbedBuilder()
                .setTitle("\ud83d\udcc8Stats")
                .addField("Guilds", shardManager.getGuilds().size() + " guilds", true)
                .addField("Users", shardManager.getUsers().size() + " users", true)
                .addBlankField(true)
                .addField("Ping", String.format("%.2f ms", shardManager.getAveragePing()), true)
                .addField("Shards", shardManager.getShardsTotal() + " shards", true)
                .addBlankField(true)
                .addField("CPU Usage", Math.round(operatingSystemMXBean.getSystemCpuLoad() * 100) + "%", true)
                .addField("Memory usage", Thread.getAllStackTraces().size() + "%", true)
                .addBlankField(true)
                .build()).queue();
    }
}

