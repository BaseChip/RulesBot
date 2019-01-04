package de.thebotdev.rulesbot.commands.guild;

import de.thebotdev.rulesbot.util.commandlib.CommandDescription;
import de.thebotdev.rulesbot.util.commandlib.Context;
import de.thebotdev.rulesbot.util.commandlib.RBCommand;
import de.thebotdev.rulesbot.util.database.RulesData;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.Optional;

import static de.thebotdev.rulesbot.Constants.CROSS;
import static de.thebotdev.rulesbot.Constants.TICK;
import static de.thebotdev.rulesbot.Main.database;
import static de.thebotdev.rulesbot.Main.shardManager;

@CommandDescription(
        name = "Restore", triggers = {"restore"},
        description = "Restore your rules", usage = {"restore"},
        longDescription = "With this command you can restore your rules in case you deleted yours"
)
public class RestoreCommand extends RBCommand {
    public void execute(Context ctx) {
        Optional<RulesData> dataOpt = database.getRules(ctx.getGuild().getId());
        if (!dataOpt.isPresent()) {
            ctx.send("You need to do the setup first").queue();
            return;
        }
        RulesData data = dataOpt.get();
        TextChannel channel = shardManager.getTextChannelById(data.getRulesChannelId());
        Message rulesmessage = channel.sendMessage(new EmbedBuilder()
                .setDescription(data.getRuleText())
                .setFooter(String.format("Please accept the rules with %s or decline them with %s", TICK, CROSS), null)
                .setColor(0x2ecd71)
                .build()).complete();
        rulesmessage.addReaction(TICK).queue();
        rulesmessage.addReaction(CROSS).queue();
        database.setRulesmessage(ctx.getGuild().getId(), rulesmessage.getId());
    }

}