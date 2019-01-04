package de.thebotdev.rulesbot.commands.listeners;

import de.thebotdev.rulesbot.util.RegisterListener;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.List;

import static de.thebotdev.rulesbot.Constants.*;

@RegisterListener
public class PatreonListener extends ListenerAdapter {
    @Override
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
        if (event.getMember().getUser().isBot()) return;
        if (event.getGuild().getIdLong() == THEBOTDEV_GUILD) {
            List roles = event.getMember().getRoles();
            boolean ap = false;
            boolean prem = false;
            for (Role role : event.getMember().getRoles()) {
                if (role.getIdLong() == ACTIVE_PATREON_ROLE) {
                    ap = true;
                }
                if (role.getIdLong() == PREMIUM_ROLE) {
                    prem = true;
                }
            }
            if (ap && !prem) {
                event.getGuild().getController().addSingleRoleToMember(
                        event.getMember(), event.getGuild().getRoleById(PREMIUM_ROLE))
                        .reason("Got Active Patreon Role").queue();
            }
        }
    }
}
