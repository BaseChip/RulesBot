package de.thebotdev.rulesbot.commands.guild;

import de.thebotdev.rulesbot.util.commandlib.*;
import de.thebotdev.rulesbot.util.database.RulesData;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;
import java.util.Optional;

import static de.thebotdev.rulesbot.Main.database;

@CommandDescription(
        name = "Edit_Message", triggers = {"edit_message", "edit_rules", "editmessage"},
        description = "change your rules message", usage = {"edit_message [new message]"},
        longDescription = "A command to change/update your server rules",
        hidden = true
)
@Checks(Check.ADMIN_ONLY)
public class EditMessageCommand extends RBCommand {
    public void execute(Context ctx, String new_message) {
        Optional<RulesData> dataOpt = database.getRules(ctx.getGuild().getId());
        if (!dataOpt.isPresent()) {
            ctx.send("Please do the setup first").queue();
            return;
        }
        RulesData data = dataOpt.get();
        if (data.isSetupComplete()) {
            TextChannel channel = ctx.getGuild().getTextChannelById(data.getRulesChannelId());
            Message msg = channel.getMessageById(data.getMessageId()).complete();
            Color col = msg.getEmbeds().get(0).getColor();
            MessageEmbed.Footer footer = msg.getEmbeds().get(0).getFooter();
            msg.editMessage(new EmbedBuilder()
                    .setDescription(new_message)
                    .setColor(col)
                    .setFooter(footer.getText(), null)
                    .build()).complete();
            ctx.send("successful").queue();
            database.setRulestext(ctx.getGuild().getId(), new_message);
        } else {
            ctx.send("you need to setup some rules first").queue();
        }
    }
}
