package de.thebotdev.rulesbot.commands.guild;

import de.thebotdev.rulesbot.util.commandlib.CommandDescription;
import de.thebotdev.rulesbot.util.commandlib.Context;
import de.thebotdev.rulesbot.util.commandlib.RBCommand;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;

import static de.thebotdev.rulesbot.Main.database;

@CommandDescription(
        name = "Data_guild", triggers = {"dataguild", "data_guild"},
        description = "see your server settings", usage = {"data_guild"},
        longDescription = "With this command you can see all your server settings we have stored about your server in our database"
)
public class DataGuildCommand extends RBCommand {

    public static final String MI6_SPECIAL_MISSION = "Missing/Deleted";

    public void execute(Context ctx) {
        String prefix = database.getPrefix(ctx.getEvent().getGuild().getId()).orElse("-");
        EmbedBuilder em = new EmbedBuilder()
                .addField("Prefix", prefix, false)
                .setColor(Color.green);

        database.getRules(ctx.getGuild().getId()).ifPresent(rules -> {
            Guild guild = ctx.getGuild();

            if (!rules.isSetupComplete())
                return;

            if (rules.getAction() == ActionEnum.KICK &&
                    !ctx.getSelfMember().hasPermission(Permission.KICK_MEMBERS)) {
                em.addField("Server Permissions", "Missing kick permissions", false);
                em.setColor(Color.red);
            } else if (rules.getAction() == ActionEnum.BAN &&
                    !ctx.getSelfMember().hasPermission(Permission.BAN_MEMBERS)) {
                em.addField("Server Permissions", "Missing ban permissions", false);
                em.setColor(Color.red);
            } else {
                em.addField("Action", rules.getAction().name(), false);
            }

            Role role = guild.getRoleById(rules.getRoleId());
            if (role == null) {
                em.addField("Role", "Role missing/deleted", false);
                em.setColor(Color.red);
            } else {
                if (!ctx.getSelfMember().canInteract(role)) {
                    em.addField("Role", role.getAsMention() + ": Rulesbot role too low.", false);
                    em.setColor(Color.red);
                } else {
                    em.addField("Role", role.getAsMention(), false);
                }
            }

            TextChannel logChannel = guild.getTextChannelById(rules.getLogChannel());
            if (logChannel == null) {
                em.addField("Log Channel", MI6_SPECIAL_MISSION, false);
                em.setColor(Color.red);
            } else {
                if (guild.getSelfMember().hasPermission(logChannel, Permission.MESSAGE_READ, Permission.MESSAGE_HISTORY)) {
                    em.addField("Log Channel", logChannel.getAsMention(), false);
                } else {
                    em.addField("Log Channel", logChannel.getAsMention() + ": Missing permissions", false);
                    em.setColor(Color.red);
                }
            }


            TextChannel rulesChannel = guild.getTextChannelById(rules.getRulesChannelId());
            if (rulesChannel == null) {
                em.addField("Channel", MI6_SPECIAL_MISSION, false);
                em.setColor(Color.red);
                return;
            } else if (guild.getSelfMember().hasPermission(rulesChannel, Permission.MESSAGE_READ, Permission.MESSAGE_HISTORY, Permission.MANAGE_EMOTES)) {
                em.addField("Channel", rulesChannel.getAsMention(), false);
            } else {
                em.addField("Channel", rulesChannel.getAsMention() + ": Missing permissions", false);
                em.setColor(Color.red);
            }

            Message mes = rulesChannel.getMessageById(rules.getMessageId()).complete();
            if (mes == null) {
                em.addField("Message", MI6_SPECIAL_MISSION, false);
                em.setColor(Color.red);
                return;
            }
            em.addField("Message", "Found", false);

        });

        ctx.send(em.build()).queue();
    }
}
