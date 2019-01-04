package de.thebotdev.rulesbot.commands.guild;

import de.thebotdev.rulesbot.util.commandlib.*;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;

import static de.thebotdev.rulesbot.Main.database;

@CommandDescription(
        name = "Setup_Reaction",
        triggers = {"setupreaction", "setup_reaction"},
        usage = {
                "setupreaction",
                "setup_reaction"
        },
        description = "Add a reaction to get a role",
        longDescription = "With this function you can have a reaction made under any message you want and can choose which role a user gets when he clicks on it."
)
@Checks(Check.ADMIN_ONLY)
public class SetupReactionCommand extends RBCommand {

    public void execute(Context ctx) {
        AdvancedWaiter awaiter = ctx.getAwaiter();
        ctx.send("Okay the setup has started! first of all mention the"
                + " channel in which your message was written and where"
                + " I should add the reaction.").queue();
        TextChannel channel = awaiter.waitForChannel(TextChannel::canTalk).join();
        ctx.send("So now please send me the message ID from the message where the reactions should been added. You can "
                + "get it by right-clicking on your message and copying it with `copy id`. This will not work until you "
                + "have activated Developer mode. (https://discordia.me/developer-mode)").queue();
        Long messageId = awaiter.waitForLong().join();
        Message message = channel.getMessageById(messageId).complete();
        Message emojiMessage = ctx.send("React to this message with the emoji I should add"
                + " to your message - if you want a non-standard Discord Emoji you need to"
                + " add this emoji to this server").complete();
        MessageReaction.ReactionEmote reaction = awaiter.waitForReaction(emojiMessage).join().getReactionEmote();
        if (reaction.isEmote() && reaction.getEmote().getGuild().getIdLong() != ctx.getGuild().getIdLong()) {
            ctx.send("**I cant find this emoji - Are you sure this emoji is added to this server?**").queue();
            return;
        }
        String emoji = reaction.getId() != null ? reaction.getId() : reaction.getName();
        ctx.send("Please mention or send me the id from the role I should add the users if they"
                + " click on the reaction").queue();
        Role role = awaiter.waitForRole().join();
        database.addReactionAction(message.getId(), reaction, role.getId());
        if (reaction.isEmote()) {
            message.addReaction(reaction.getEmote()).queue();
        } else {
            message.addReaction(reaction.getName()).queue();
        }
        ctx.send("Added Emoji").queue();

    }

}
