package de.thebotdev.rulesbot.commands.guild;

import de.thebotdev.rulesbot.util.commandlib.CommandDescription;
import de.thebotdev.rulesbot.util.commandlib.Context;
import de.thebotdev.rulesbot.util.commandlib.RBCommand;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Role;

@CommandDescription(
        name = "Roles", triggers = {"role", "roles", "role_id", "server_roles"},
        description = "See all your roles and the ids", usage = {"roles"},
        longDescription = "This command lists all of your roles you have on this guild and the corresponding ids"
)
public class RolesCommand extends RBCommand {
    public void execute(Context ctx) {
        String roles = "```";
        for (Role role : ctx.getGuild().getRoles()) {
            roles = String.format("%s%s \u00bb %s\n", roles, role.getName(), role.getId());
            if (roles.length() >= 1900) {
                roles = roles + "```";
                ctx.send(new EmbedBuilder()
                        .setDescription(roles)
                        .build()).queue();
                roles = "```";
            }
        }
        roles = roles + "```";
        ctx.send(new EmbedBuilder()
                .setDescription(roles)
                .build()).queue();
    }
}
