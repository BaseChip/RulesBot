package de.thebotdev.rulesbot.commands.beta;

import de.thebotdev.rulesbot.util.commandlib.*;
import de.thebotdev.rulesbot.util.database.RulesData;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.requests.RestAction;

import java.util.Optional;
import java.util.stream.Stream;

import static de.thebotdev.rulesbot.Main.database;

@CommandDescription(
        name = "Setup_Channel", triggers = {"setup_channel", "setupchannel"},
        description = "the short one", usage = {"setup_channel [channel_mention] <as much channels as you want>"},
        longDescription = "The long one"
)
@Checks({Check.ADMIN_ONLY, Check.BETA_ONLY})
public class SetupChannelCommand extends RBCommand {
    public void execute(Context ctx, TextChannel... channels) {
        Optional<RulesData> rulesOpt = database.getRules(ctx.getGuild().getId());
        if (!rulesOpt.isPresent()) {
            ctx.send("You need to setup some rules first in order to use this command").queue();
            return;
        }
        Stream.of(channels)
                .flatMap(channel -> Stream.of(
                        channel.putPermissionOverride(ctx.getGuild().getPublicRole())
                                .setDeny(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE),
                        channel.putPermissionOverride(ctx.getGuild().getRoleById(rulesOpt.get().getRoleId()))
                                .setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE)
                ))
                .parallel()
                .forEach(RestAction::complete);
        ctx.send("Successful! Changed the mentioned channels").queue();

    }
}
