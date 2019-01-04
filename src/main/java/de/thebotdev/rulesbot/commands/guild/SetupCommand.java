package de.thebotdev.rulesbot.commands.guild;

import de.thebotdev.rulesbot.util.commandlib.*;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;

import static de.thebotdev.rulesbot.Constants.*;
import static de.thebotdev.rulesbot.Main.database;

@CommandDescription(
        name = "Setup",
        description = "Sets up rules in your server",
        longDescription = "This is the main feature of this bot which allows you to create a rule message that new users must first accept in order to gain access to the server. When this command is executed, the bot will ask for the data it needs to create these rules.",
        triggers = {"setup", "rules"},
        usage = {
                "setup",
                "rules"
        }
)
@Checks(Check.ADMIN_ONLY)
public class SetupCommand extends RBCommand {
    private static boolean roleHigh(Context ctx, Role role) {
        return ctx.getSelfMember().canInteract(role);
    }

    public void execute(Context context) throws NoConverterFoundException {
        AdvancedWaiter awaiter = context.getAwaiter();
        context.send("The setup to create rules has started successfully. First please mark the channel in which I should send the rules later (#yourchannel e.g. #rules). Please note that in this channel I need both the read and write permission as well as the embed link permission.\nPlease keep in mind that currently no emojis are allowed in any messages (rule message / onjoin message / ...)").queue();
        TextChannel rulesChannel = awaiter.waitForChannel(channel -> channel.getGuild() == context.getGuild() && context.getSelfMember().hasPermission(channel, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS)).join();
        context.send("So now please send me the rules you want to have. It is important to note that this can be a maximum of 2,000 characters long, as this is so limited by Discord.").queue();
        String rulesText = awaiter.waitForText().join();
        Message actionMessage = context.send(String.format("Well, what should happen if a user does not accept the rules. I can kick or ban those users. If you want me to ban the user please react with %s. If you want me to kick him please react with %s", HAMMER, BOOT)).complete();
        try {
            actionMessage.addReaction(HAMMER).complete();
            actionMessage.addReaction(BOOT).complete();
        } catch (Exception e) {
            context.send("**:exclamation: please try the setup again and give the bot add reaction permission**\n").queue();
            return;
        }
        ActionEnum action = ActionEnum.fromEmote(awaiter.waitForReaction(actionMessage, e -> (HAMMER + BOOT).contains(e.getReactionEmote().getName())).join().getReactionEmote().getName());
        context.send(" What role should I assign to the user if he or she accepts the rules? You could answer with the a mention of the role (@role) or the corresponding id (see next message)").queue();
        RolesCommand roles_sender = new RolesCommand();
        roles_sender.execute(context);
        Role role = awaiter.waitForRole().join();
        context.send("Do you want a notification for new members on join? y/n").queue();
        boolean joinNotification = awaiter.waitForBool().join();
        String joinNotificationText = "";
        if (joinNotification) {
            context.send("So please send me the on welcome message for new users").queue();
            joinNotificationText = awaiter.waitForText().join();
        }
        context.send("Do you want a message for users who decline the rules? y/n").queue();
        boolean declineNotification = awaiter.waitForBool().join();
        String declineNotificationText = "";
        if (declineNotification) {
            context.send("Okay please send me now the text the user should get").queue();
            declineNotificationText = awaiter.waitForText().join();
        }

        context.send("Please mention now the channel where I should send all log files in, i. e. who has not accepted the rules or who removed the reaction.").queue();
        TextChannel logs = awaiter.waitForChannel(channel -> channel.getGuild() == context.getGuild() && context.getSelfMember().hasPermission(channel, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS)).join();

        context.send("Should I also remove a role when the user accept the rules? y/n").queue();
        Role removerole = null;
        if (awaiter.waitForBool().join()) {
            context.send("So then please mention now the role or send me the id from the role I should remove when new users accept the rules").queue();
            removerole = awaiter.waitForRole().join();
        }
        Color customembed_color = new Color(0x2ecd71);
        String customfooter_text = String.format("Please accept the rules with %s or decline them with %s", TICK, CROSS);
        if (Check.PREMIUM_ONLY.check(context)) {
            context.send("Oh you got **premium** so **thanks for supporting us**! Therefore you can choose a custom footer text. Do you want this? y/n").queue();
            boolean customfooter = awaiter.waitForBool().join();
            if (customfooter) {
                context.send("Please send me now the footers new text").queue();
                customfooter_text = awaiter.waitForText().join();
            }
            context.send("Because you have chosen premium you can also change the embed color of the rules message. Would you like that? y/n").queue();
            boolean customembed = awaiter.waitForBool().join();
            if (customembed) {
                context.send("Okay please send me the embed color you would like to have as hex code - you can get it here(https://www.w3schools.com/colors/colors_picker.asp) and it should like `#00ff40`").queue();
                customembed_color = awaiter.waitForColor().join();
            }
        } else {
            context.send(new EmbedBuilder()
                    .setDescription(":warning: For more features like changing the embed color or your own footer text, have a look at [Premium](https://patreon.com/TheBotDev)")
                    .build()).queue();
        }
        Message rulesmessage;
        try {
            rulesmessage = rulesChannel.sendMessage(new EmbedBuilder()
                    .setDescription(rulesText)
                    .setColor(customembed_color)
                    .setFooter(customfooter_text, null)
                    .build()).complete();
            rulesmessage.addReaction(TICK).queue();
            rulesmessage.addReaction(CROSS).queue();
        } catch (Exception e) {
            context.send("**:exclamation: I dont have all perms in the ruleschannel**").queue();
            return;
        }
        database.setRules(context.getGuild().getId(), rulesChannel, rulesText, action, logs, role, rulesmessage, joinNotification, joinNotificationText, declineNotification, declineNotificationText, removerole, context.getGuild().getName());
        if (roleHigh(context, role) && roleHigh(context)) {
            context.send(new EmbedBuilder()
                    .setColor(Color.green)
                    .setDescription("**Setup done**")
                    .build()).queue();
        } else if (!roleHigh(context, role) && !roleHigh(context)) {
            context.send(new EmbedBuilder()
                    .setColor(Color.red)
                    .setDescription("**:exclamation: Setup done with some errors you need to fix or the bot cant work**\n•The role of the bot must be above the role it should assign.\n•I need kick and ban rights to kick/ban users if they reject the rules")
                    .build()).queue();
        } else if (!roleHigh(context, role) && roleHigh(context)) {
            context.send(new EmbedBuilder()
                    .setColor(Color.red)
                    .setDescription("** :exclamation: Setup done with some errors you need to fix or the bot cant work**\n•The role of the bot must be above the role it should assign.")
                    .build()).queue();
        } else if (roleHigh(context, role) && !roleHigh(context)) {
            context.send(new EmbedBuilder()
                    .setColor(Color.red)
                    .setDescription("**:exclamation: Setup done with some errors you need to fix or the bot cant work**\n•I need kick and ban rights to kick/ban users if they reject the rules")
                    .build()).queue();
        }

    }

    private boolean roleHigh(Context ctx) {
        return ctx.getSelfMember().hasPermission(Permission.BAN_MEMBERS, Permission.KICK_MEMBERS);
    }

}
