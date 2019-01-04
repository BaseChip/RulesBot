package de.thebotdev.rulesbot.commands.admin;

import de.thebotdev.rulesbot.util.commandlib.*;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static de.thebotdev.rulesbot.Constants.VERSION;
import static de.thebotdev.rulesbot.Main.shardManager;
import static de.thebotdev.rulesbot.util.StreamUitl.chunkedByCount;

@CommandDescription(
        name = "update", triggers = {"update", "message_all"},
        longDescription = "You message all server owner from every guild the bot is on", description = "Remove this command from help",
        usage = {"update <message>"},
        hidden = true
)
@Checks(Check.DEVELOPER_ONLY)
public class UpdateCommand extends RBCommand {
    public void execute(Context ctx, String message) {
        Map<User, List<Guild>> owners = new HashMap<>();
        for (Guild guild : shardManager.getGuilds()) {
            owners.computeIfAbsent(guild.getOwner().getUser(), ignored -> new ArrayList<>()).add(guild);
        }
        int total = owners.size();
        //owners.values().stream().flatMap(Collection::stream).map(guild -> guild.getDefaultChannel().createInvite().complete()).map(Invite::getURL).forEach(System.out::println);
        AtomicInteger mesCounter = new AtomicInteger();
        Message mes = ctx.send(getMessage(mesCounter.get(), total)).complete();
        chunkedByCount(owners.entrySet().stream(), 100).forEach(chunk ->
        {
            chunk.map(entry -> {
                try {
                    return entry.getKey().openPrivateChannel().complete().sendMessage(new EmbedBuilder()
                            .setTitle("Update - v " + VERSION)
                            .setDescription(message + "\n**You get this message because your the owner of the following guilds:**\n" + entry.getValue().stream().map(Guild::getName).collect(Collectors.joining(",\n")))
                            .build()).complete();
                } catch (Exception e) {
                }
                return null;
            }).forEach(ignored -> mesCounter.incrementAndGet());
            mes.editMessage(getMessage(mesCounter.get(), total)).queue();
        });
    }

    private String getMessage(int count, int total) {
        if (count == total) {
            return String.format("Sending done. %s Total messages: `%d`", shardManager.getEmoteById(525006166338568195L).getAsMention(), total);
        }
        return String.format("Sending %s - progress `%.2f`%% - messages: `%d`", shardManager.getEmoteById(524992229677203456L).getAsMention(), count * 100f / total, count);
    }
}
